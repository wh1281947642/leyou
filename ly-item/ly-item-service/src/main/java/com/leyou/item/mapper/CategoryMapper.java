package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * <p>
 * <code>CategoryMapper</code>
 * </p>
 * 分类管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:30
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long>{
}
