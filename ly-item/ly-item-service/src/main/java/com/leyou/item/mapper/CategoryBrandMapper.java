package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.CategoryBrand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * <p>
 * <code>CategoryBrandMapper</code>
 * </p>
 *  商品管理 分类管理 关联表
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/25 17:40
 */
public interface CategoryBrandMapper extends Mapper<CategoryBrand>, IdListMapper<CategoryBrand, Long> {

}