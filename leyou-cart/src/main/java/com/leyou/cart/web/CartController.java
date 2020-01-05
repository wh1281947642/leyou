package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.pojo.CartList;
import com.leyou.cart.service.CartService;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * <code>CartController</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/05 13:39
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 13:51
     * @param
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 合并购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 13:51
     * @param
     * @return
     */
    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCart(@RequestBody CartList cartList) {
        List<Cart> carts = JsonUtils.parseList(cartList.getCartListStr(), Cart.class);
        this.cartService.mergeCart(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车列表
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 14:46
     * @param
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCarts() {
        List<Cart> carts = this.cartService.queryCarts();
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车数量
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 15:11
     * @param
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart) {
        this.cartService.updateNum(cart);
        return ResponseEntity.noContent().build();
    }

    /**
     *  删除购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 15:37
     * @param
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}