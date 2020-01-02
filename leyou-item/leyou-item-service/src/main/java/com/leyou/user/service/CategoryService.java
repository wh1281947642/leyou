package com.leyou.user.service;

import com.leyou.user.mapper.CategoryMapper;
import com.leyou.user.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <code>CategoryService</code>
 * </p>
 *  分类管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:28
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/23 16:33
     * @param
     * @return 
     */
    public List<Category> queryCategoryListByPid(Long pid) {
        // 查询条件，mapper会把对象中的非空属性作为查询条件
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)) {
            //throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据商品分类ids查询商品分类名称集合
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/12/14 14:53
     * @param ids
     * @return
     */
    public List<String> queryNameSByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> list = categories.stream().map(category -> category.getName()).collect(Collectors.toList());
        return list;
    }
}
