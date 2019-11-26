package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryBrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.CategoryBrand;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>
 * <code>BrandService</code>
 * </p>
 *  品牌管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:28
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/24 17:11
     * @param key 搜索条件
     * @param page 页数
     * @param rows 每页多少条数据
     * @param sortBy 根据什么排序
     * @param desc 升序还是降序
     * @return PageResult<Brand>
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }

        // 添加分页条件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            //example.setOrderByClause("id desc");
            example.setOrderByClause(orderByClause);
        }
        List<Brand> brands = brandMapper.selectByExample(example);
       //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }


    /**
     * 品牌新增
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/25 17:17
     * @param
     * @return
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand
        brandMapper.insert(brand);

        // 在新增中间表
        for (Long cid : cids) {
            // 新增brand，brand的id会自动回写
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandId(brand.getId());
            categoryBrand.setCategoryId(cid);
            categoryBrandMapper.insertSelective(categoryBrand);
        }
    }


    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;

    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> list = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
