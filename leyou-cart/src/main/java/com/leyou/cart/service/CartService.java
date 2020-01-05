package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.user.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <code>CartService</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/05 13:39
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart:";

    /**
     * 添加购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 13:51
     * @param
     * @return
     */
    public void addCart(Cart cart) {

        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //新增或者跟新购物车
        addOrUpdateCart(userInfo, hashOps, cart);
    }

    /**
     * 合并购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 13:51
     * @param
     * @return
     */
    public void mergeCart(List<Cart> carts) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //遍历购物车
        carts.forEach(cart -> {
            //新增或者跟新购物车
            addOrUpdateCart(userInfo, hashOps, cart);
        });
    }

    /**
     * 新增或者跟新购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 16:58
     * @param
     * @return
     */
    private void addOrUpdateCart(UserInfo userInfo, BoundHashOperations<String, Object, Object> hashOps, Cart cart) {
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断当前的商品是否在购物车中
        if (hashOps.hasKey(key)){
            //在，更新数量
            String cartJson = hashOps.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        }else {
            //不在，新增购物车
            Sku sku = this.goodsClient.querySkuByskuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
        }
        hashOps.put(key, JsonUtils.serialize(cart));
    }

    /**
     * 查询购物车列表
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 14:46
     * @param
     * @return
     */
    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户书否有购物车记录
        if(!this.stringRedisTemplate.hasKey(KEY_PREFIX + userInfo.getId())){
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //获取购物车Map中所有cart集合
        List<Object> cartsJson = hashOps.values();
        //如果购物车集合为空
        if (CollectionUtils.isEmpty(cartsJson)){
            return null;
        }
        //把List<Object>集合转化为List<Cart>集合
        List<Cart> carts = cartsJson.stream().map(cartJson -> {
            Cart cart = JsonUtils.parse(cartJson.toString(), Cart.class);
            return cart;
        }).collect(Collectors.toList());
        return carts;
    }

    /**
     * 修改购物车数量
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 15:11
     * @param
     * @return
     */
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Integer num = cart.getNum();
        String key = cart.getSkuId().toString();
        //判断用户书否有购物车记录
        if(!this.stringRedisTemplate.hasKey(KEY_PREFIX + userInfo.getId())){
            return ;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String cartJson = hashOps.get(key).toString();
        cart = JsonUtils.parse(cartJson , Cart.class);
        cart.setNum(num);
        hashOps.put(key, JsonUtils.serialize(cart));
    }

    /**
     *  删除购物车
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/05 15:37
     * @param
     * @return
     */
    public void deleteCart(String skuId) {
        // 获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        hashOps.delete(skuId);
    }


}
