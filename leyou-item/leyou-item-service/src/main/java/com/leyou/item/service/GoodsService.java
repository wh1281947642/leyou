package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.user.pojo.*;
import com.leyou.user.vo.SpuVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <code>GoodsService</code>
 * </p>
 * 商品查询
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/14 13:45
 */
@Slf4j
@Service
public class GoodsService {

    /***
     * 标准产品单位，商品集
     */
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 根据条件分页查询spu
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/14 14:07
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Transactional
    public PageResult<SpuVo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if (StringUtils.isNoneBlank(key)){
            criteria.andLike("title","%"+ key + "%");
        }
        //添加上下架的条件
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加分页
        PageHelper.startPage(page, rows);
        //执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //转换成SpuVo集合
        List<SpuVo> spuVos = spus.stream().map(spu -> {
            SpuVo spuVo = new SpuVo();
            BeanUtils.copyProperties(spu, spuVo);
            //查询分类名称
            List<String> names = this.categoryService.queryNameSByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuVo.setCname(StringUtils.join(names, "-"));
            //查询品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuVo.setBname(brand.getName());
            return spuVo;
        }).collect(Collectors.toList());

        //返回PageResult<SpuVo>
        PageResult<SpuVo> result = new PageResult<>(pageInfo.getTotal(), spuVos);
        return  result;
    }

    /**
     * 新增商品 （json对象加上@RequestBody）
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 14:46
     * @param
     * @return
     */
    public void saveGoods(SpuVo spuVo) {
        Date date = new Date();
        //1.先新增spu
        spuVo.setId(null);
        //是否上架
        spuVo.setSaleable(true);
        //是否可用
        spuVo.setValid(true);
        //创建时间
        spuVo.setCreateTime(date);
        //更新时间
        spuVo.setLastUpdateTime(date);
        this.spuMapper.insertSelective(spuVo);

        //2.再去新增spuDetail
        SpuDetail spuDetail = spuVo.getSpuDetail();
        //主键回显
        spuDetail.setSpuId(spuVo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        saveSkuAndStock(spuVo);

        //发送消息
        sendMessage(spuVo.getId(),"insert");
    }

    /**
     * 更新商品
     * @description json对象加上@RequestBody
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 14:46
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuVo spuVo) {
        //根据spuId查询要删除的skus
        Sku record = new Sku();
        record.setSpuId(spuVo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            //1.先删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
            //2.在删除sku
            Sku skuOld = new Sku();
            skuOld.setSpuId(spuVo.getId());
            this.skuMapper.delete(skuOld);
        });
        //3.新增sku
        //4.新增stock
        this.saveSkuAndStock(spuVo);
        //5.更新spu和spuDetail
        spuVo.setCreateTime(null);
        spuVo.setLastUpdateTime(new Date());
        spuVo.setValid(null);
        spuVo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuVo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuVo.getSpuDetail());

        sendMessage(spuVo.getId(),"update");
    }

    private void saveSkuAndStock(SpuVo spuVo) {
        Date date = new Date();
        List<Sku> skus = spuVo.getSkus();
        skus.forEach(sku -> {
            //3.新增sku
            sku.setId(null);
            sku.setSpuId(spuVo.getId());
            sku.setCreateTime(date);
            sku.setLastUpdateTime(date);
            this.skuMapper.insertSelective(sku);
            //4.再去新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:43
     * @param spuId
     * @return  SpuDetail
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return  this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     *  根据spuId查询sku集合
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:50
     * @param id
     * @return List<Sku>
     */
    public List<Sku> querySkusBySpuId(Long id) {
        Sku record = new Sku();
        record.setSpuId(id);
        List<Sku> skus = skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 根据spuId查询spu
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:10
     * @param id ：spuId
     * @return 
     */
    public Spu querySpuByid(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据skuId查询sku
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:10
     * @param skuId ：skuId
     * @return
     */
    public Sku querySkuByskuId(Long skuId) {
        return this.skuMapper.selectByPrimaryKey(skuId);
    }

    /**
     * 封装一个发送消息的方法
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:11
     * @param
     * @return 
     */
    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
