package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart:";

    /**
     * 添加购物车
     * @Param: [cart]
     * @Return:
     **/
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录 ，
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String skuId = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //当前的商品是否在购物车之内
        if(hashOperations.hasKey(skuId)){
            //在,更新数量
            String cartJson = hashOperations.get(skuId).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(num+cart.getNum());
        }else{
            //不在， 新增购物车
            Sku sku = this.goodsClient.querySkuById(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
        }
        hashOperations.put(skuId,JsonUtils.serialize(cart));
    }

    /**
     * 查询购物车
     * @Param: []
     * @Return:
     **/
    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //先去判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX + userInfo.getId())){
            return null;
        }
        //获取用户的购物车
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //获取购物车map中的所有cart集合
        List<Object> cartsJson = hashOperations.values();
        //如果购物车记录为空
        if(CollectionUtils.isEmpty(cartsJson)){
            return null;
        }
        //把List<Object> 对象转化为 List<cart>对象
        List<Cart> carts = cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());
        return carts;
    }
    /**
     * 更新数量
     * @Param: [cart]
     * @Return:
    **/
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //先去判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX + userInfo.getId())){
            return;
        }
        //获取num保存,防止覆盖
        Integer num = cart.getNum();
        //获取用户的购物车
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    /**
     * 删除购物车中的东西
     * @Param: [skuId]
     * @Return:
     **/
    public void deleteCart(String skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        hashOperations.delete(skuId);
    }
}
