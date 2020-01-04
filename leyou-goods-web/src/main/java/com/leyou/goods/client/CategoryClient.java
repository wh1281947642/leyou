package com.leyou.goods.client;

import com.leyou.user.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>CategoryClient</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/04 15:00
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
