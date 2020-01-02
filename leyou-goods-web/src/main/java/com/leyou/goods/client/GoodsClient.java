package com.leyou.goods.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:06
 * @Description:
 */
@FeignClient("user-service")
public interface GoodsClient extends GoodsApi{
}
