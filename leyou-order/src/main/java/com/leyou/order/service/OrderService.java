package com.leyou.order.service;

import com.leyou.common.vo.PageResult;
import com.leyou.order.pojo.Order;

/**
 * @Author: 98050
 * @Time: 2018-10-27 16:34
 * @Feature:
 */
public interface OrderService {
    /**
     * 订单创建
     * @param order
     * @return
     */
    Long createOrder(Order order);

    /**
     * 根据订单号查询订单
     * @param id
     * @return
     */
    Order queryOrderById(Long id);

    /**
     * 分页查询用户订单
     * @param page
     * @param rows
     * @param status
     * @return
     */
    PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status);

    /**
     * 更改订单状态
     * @param id
     * @param status
     * @return
     */
    Boolean updateOrderStatus(Long id, Integer status);
}
