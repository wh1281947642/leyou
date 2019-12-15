package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import com.leyou.item.vo.SpuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * <code>GoodsController</code>
 * </p>
 * 商品查询
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/14 13:46
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;



    /**
     * 根据条件分页查询spu
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/12/14 14:07
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuVo>> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        PageResult<SpuVo> result = this.goodsService.querySpuByPage(key,saleable,page,rows);
        if(result==null||CollectionUtils.isEmpty(result.getItems())){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     * @description json对象加上@RequestBody
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 14:46
     * @param
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuVo spuVo) {
        this.goodsService.saveGoods(spuVo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品
     * @description json对象加上@RequestBody
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 14:46
     * @param
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuVo spuVo) {
        this.goodsService.updateGoods(spuVo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据spuId查询spuDetail
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:43
     * @param spuId
     * @return  SpuDetail
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId) {
        SpuDetail spuDetail = this.goodsService.querySpuDetailBySpuId(spuId);
        if (spuDetail == null) {
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     *  根据spuId查询sku集合
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:50
     * @param spuId
     * @return List<Sku>
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus = this.goodsService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuByid(id));
    }

}
