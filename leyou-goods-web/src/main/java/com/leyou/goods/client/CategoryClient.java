package com.leyou.goods.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 15:50
 * @Description:
 */
@FeignClient("user-service")
public interface CategoryClient extends CategoryApi {
}
