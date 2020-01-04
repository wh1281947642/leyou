package com.leyou.goods.client;

import com.leyou.user.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>BrandClient</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/04 15:00
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi{
}
