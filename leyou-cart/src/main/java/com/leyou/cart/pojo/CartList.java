package com.leyou.cart.pojo;

import java.io.Serializable;

/**
 * <p>
 * <code>CartList</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/05 16:43
 */
public class CartList implements Serializable {

    private String cartListStr;

    public String getCartListStr() {
        return cartListStr;
    }

    public void setCartListStr(String cartListStr) {
        this.cartListStr = cartListStr;
    }
}
