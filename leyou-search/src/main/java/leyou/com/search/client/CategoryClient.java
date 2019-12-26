package leyou.com.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * <code>CategoryClient</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 15:06
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
