package com.leyou.user.api;

import com.leyou.user.pojo.Brand;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * <code>BrandApi</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 14:54
 */
@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据id查询品牌
     * @description T
     * @author huiwang45@iflytek.com
     * @date 2019/12/25 16:49
     * @param
     * @return
     */
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);
}
