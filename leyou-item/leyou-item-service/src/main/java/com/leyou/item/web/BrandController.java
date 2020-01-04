package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.user.pojo.Brand;
import com.leyou.item.service.BrandService;
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
     * 品牌新增
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/25 17:17
     * @param
     * @return
     */
    @PostMapping  // 传入 "1,2,3"的字符串可以解析为列表
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id查询品牌列表
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/14 16:28
     * @param
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid") Long cid) {
        List<Brand> brands = this.brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据id查询品牌
     * @description T
     * @author huiwang45@iflytek.com
     * @date 2019/12/25 16:49
     * @param
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        Brand brand = this.brandService.queryBrandById(id);
        if(brand == null){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(brand);
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
