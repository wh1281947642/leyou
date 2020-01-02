package com.leyou.user.web;

import com.leyou.user.pojo.Category;
import com.leyou.user.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * <code>CustomerStateWorkerOrderService</code>
 * </p>
 *  分类管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:29
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询子节点
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/23 16:17
     * @param pid
     * @return List<Category>
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid) {
            if(pid == null || pid<0){
                //400:参数不合法
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                return ResponseEntity.badRequest().build();
            }
            List<Category> categorys = this.categoryService.queryCategoryListByPid(pid);
            if(CollectionUtils.isEmpty(categorys)){
                //404:资源未找到
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                return ResponseEntity.notFound().build();
            }
            //200:查询成功
            return ResponseEntity.ok(categorys);
    }


    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = this.categoryService.queryNameSByIds(ids);
        if(CollectionUtils.isEmpty(names)){
            //404:资源未找到
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
