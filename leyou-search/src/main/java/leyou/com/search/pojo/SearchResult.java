package leyou.com.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * <code>SearchResult</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/28 14:58
 */
@Data
public class SearchResult extends PageResult<Goods> {

    /***
     * 分类的集合
     */
    private List<Category> categories;
    /***
     * 品牌的集合
     */
    private List<Brand> brands;
    /***
     * 规格参数过滤条件
     */
    private List<Map<String,Object>> specs;

    public SearchResult(){};

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
