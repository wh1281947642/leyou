package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>GoodsClient</code>
 * </p>
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/25 17:33
 */
@FeignClient("user-service")
public interface GoodsClient extends GoodsApi{
}
