package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.vo.SpuVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * <code>GoodsApi</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 14:45
 */
public interface GoodsApi {

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
    public PageResult<SpuVo> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows);

    /**
     *  根据spuId查询sku集合
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:50
     * @param spuId
     * @return List<Sku>
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);

    /**
     * 根据spuId查询spuDetail
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/15 15:43
     * @param spuId
     * @return  SpuDetail
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId) ;

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);
}
