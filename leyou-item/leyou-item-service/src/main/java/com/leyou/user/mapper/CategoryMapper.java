package com.leyou.user.mapper;

import com.leyou.user.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
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
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {
}
