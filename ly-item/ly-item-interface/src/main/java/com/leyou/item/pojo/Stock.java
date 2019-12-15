package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * <code>Stock</code>
 * </p>
 *  库存
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/15 14:39
 */
@Data
@Table(name = "tb_stock")
public class Stock {
    @Id
    private Long skuId;
    private Integer seckillStock;// 秒杀可用库存
    private Integer seckillTotal;// 已秒杀数量
    private Integer stock;// 正常库存
}
