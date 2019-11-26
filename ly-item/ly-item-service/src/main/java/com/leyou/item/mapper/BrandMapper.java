package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * <p>
 * <code>BrandMapper</code>
 * </p>
 *  品牌管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:30
 */
public interface BrandMapper extends Mapper<Brand>, IdListMapper<Brand, Long> {

    @Select("SELECT b.* FROM tb_category_brand cb LEFT JOIN tb_brand b ON cb.brand_id = b.id WHERE cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
}
