package com.leyou.goods.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:20
 * @Description:
 */
@FeignClient("user-service")
public interface SpecificationClient extends SpecificationApi{
}
