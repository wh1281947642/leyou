package com.leyou.goods.client;

import com.leyou.user.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:21
 * @Description:
 */
@FeignClient("user-service")
public interface BrandClient extends BrandApi{
}
