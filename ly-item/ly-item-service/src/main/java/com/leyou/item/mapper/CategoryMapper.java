package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 
 *
 * @param
 * @return 
 * @description TODO
 * @author huiwang45@iflytek.com
 * @date 2019/11/23 16:36
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long>{
}
