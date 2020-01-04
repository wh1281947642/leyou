package com.leyou.search.client;

import com.leyou.user.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>BrandClient</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 15:13
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi{
}
