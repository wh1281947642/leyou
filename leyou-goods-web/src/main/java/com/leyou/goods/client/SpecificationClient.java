package com.leyou.goods.client;

import com.leyou.user.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>SpecificationClient</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/04 14:58
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi{
}
