package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.vo.SpuVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.soap.Detail;
import java.beans.Transient;
import java.util.ArrayList;
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
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 根据条件分页查询spu
     * @description TODO
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

    public PageResult<Spu> querySpuPage(Integer page, Integer rows, Boolean saleable, String key) {
        // 1、查询SPU
        // 分页,最多允许查100条
        PageHelper.startPage(page, Math.min(rows, 200));

        // 创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 是否过滤上下架
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }

        // 是否模糊查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        // 默认排序
        example.setOrderByClause("last_update_time DESC");

        // 查询
        List<Spu> spus = spuMapper.selectByExample(example);

        // 判断
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        // 解析分类和
        //loadCategoryAndBrandName(spus);

        // 解析分页的结果
        PageInfo<Spu> info = new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(), spus);
    }

    /*private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            // 处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            // 处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    @Transactional
    public void saveGoods(Spu spu) {
        // 新增spu
        saveSpu(spu);
        // 新增detail
        saveSpuDetail(spu);
        // 新增sku和库存
        saveSkuAndStock(spu);

        // 发送消息
        this.sendMessage(spu.getId(), "insert");
    }*/

    /*private void saveSkuAndStock(Spu spu) {
        List<Stock> list = new ArrayList<>();
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            // 保存sku
            sku.setSpuId(spu.getId());
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            list.add(stock);
        }
        // 批量新增库存
        int count = stockMapper.insertList(list);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

    }

    private void saveSpuDetail(Spu spu) {
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        int count = spuDetailMapper.insert(detail);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }*/

    private void saveSpu(Spu spu) {
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    public SpuDetail querySpuDetailByid(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkusBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        return skus;
    }

    public Spu querySpuByid(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new  LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spu;
    }

    // 封装一个发送消息的方法
    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }


}
