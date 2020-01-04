package com.leyou.goods.client;

import com.leyou.user.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>GoodsClient</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/04 14:55
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi{
}
