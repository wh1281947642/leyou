package leyou.com.search.web;

import com.leyou.common.vo.PageResult;
import leyou.com.search.pojo.Goods;
import leyou.com.search.pojo.SearchRequest;
import leyou.com.search.pojo.SearchResult;
import leyou.com.search.service.SearchService;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <code>SearchController</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/27 15:59
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 根据key查询分页集合
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/27 15:59
     * @param
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
        SearchResult result = this.searchService.search(request);
        if(result ==null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

}
