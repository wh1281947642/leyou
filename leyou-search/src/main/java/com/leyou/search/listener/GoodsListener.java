package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <code>GoodsListener</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/01 15:23
 */
@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    /**
     * 保存或更新
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:31
     * @param
     * @return
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"user.insert", "user.update"}))
    public void save(Long id) throws Exception {
        if (id == null) {
            return;
        }
        this.searchService.save(id);
    }

    /**
     * 删除
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:32
     * @param
     * @return
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "user.delete"))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        // 删除页面
        this.searchService.delete(id);
    }
}
