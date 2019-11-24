package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * <code>BrandController</code>
 * </p>
 * 品牌管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:09
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

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
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            //？后面的参数用@RequestParam（""）接收
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc) {
        PageResult<Brand> result = brandService.queryBrandByPageAndSort(page,rows,sortBy,desc, key);
        if (CollectionUtils.isEmpty(result.getItems())) {
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping  // 传入 "1,2,3"的字符串可以解析为列表
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));
    }
    /**
     * 根据id列表查询品牌列表
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }

}
