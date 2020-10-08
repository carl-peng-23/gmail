package com.atecut.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atecut.gmall.bean.CartInfo;
import com.atecut.gmall.bean.SkuInfo;
import com.atecut.gmall.cart.constant.CartConst;
import com.atecut.gmall.cart.mapper.CartInfoMapper;
import com.atecut.gmall.config.RedisUtil;
import com.atecut.gmall.service.CartService;
import com.atecut.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Reference
    ManageService manageService;

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Transactional
    // 登录状态添加购物车
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        // 1、先查询购物车中是否有相同的商品，如果有，则数量相加，没有则直接添加到数据库
        // 2、更新缓存
        // 根据skuId和userId查询购物车商品
        // SELECT * FROM cartInfo WHERE userId = ? and skuId = ?
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        // 有相同的商品
        if (cartInfoExist != null) {
            // 更新数量
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            // 给skuPrice一个初始化的值
            cartInfoExist.setSkuPrice(cartInfoExist.getSkuPrice());
            // 更新数据
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
        } else {
            // 没有相同的商品
            // 根据skuId查询商品详情
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            // 属性赋值
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            // 添加到数据库
            cartInfoMapper.insertSelective(cartInfo);
            cartInfoExist = cartInfo;
        }
        // 同步缓存
        Jedis jedis = redisUtil.getJedis();
        // 定义Key    user:userId:cart
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        // 采用hash存放购物车数据，一个用户下有多个skuId，一个skuId对应一个CartInfo对象
        jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        /*
            1、先查缓存
            2、缓存没有就查数据库，存入缓存
         */
        List<CartInfo> cartInfoList = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        // 定义Key    user:userId:cart
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        List<String> hvals = jedis.hvals(cartKey);
        // 如果缓存不为空
        if (hvals != null && hvals.size() > 0) {
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            // 查看的时候应该排序！真实项目按照更新时间排序
            cartInfoList.sort(((o1, o2) -> {
                return Integer.valueOf(o1.getId()) - Integer.valueOf(o2.getId());
            }));
        } else {
            // 缓存为空，查询数据库
            cartInfoList = loadCartCache(userId);
        }
        return cartInfoList;
    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        // 根据userId获取购物车数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 开始合并
        boolean flag = false; // false:DB中没有此款商品，true:DB中有此款商品，数量相加
        for (CartInfo cartInfoCK : cartListFromCookie) {
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())) {
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum() + cartInfoDB.getSkuNum());
                    // 合并之后修改数据库
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    // 更新redis
                    checkCart(cartInfoCK.getSkuId(), cartInfoCK.getIsChecked(), userId);
                    flag = true;
                }
            }
            if (!flag) {
                // 添加到数据库
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        // 最终将合并之后的数据返回
        List<CartInfo> cartInfoList = loadCartCache(userId);
        return cartInfoList;
    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        /*
            1、获取jedis客户端
            2、获取购物车集合
            3、修改skuId商品的勾选状态
            4、写回购物车
            ======================
            5、新建一个购物车来存储勾选的商品。方便结算
         */
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        String cartInfoJson = jedis.hget(cartKey, skuId);
        if (!StringUtils.isEmpty(cartInfoJson)) {
            // 将redis中该商品转为对象
            CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
            // 修改状态
            cartInfo.setIsChecked(isChecked);
            // 写回redis
            jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfo));
            // 新建一个购物车的key
            String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
            if (isChecked.equals("1")){
                jedis.hset(userCheckedKey,skuId,JSON.toJSONString(cartInfo));
            }else{
                jedis.hdel(userCheckedKey,skuId);
            }
        }
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        // redis中购物车的key
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        List<String> hvals = jedis.hvals(userCheckedKey);
        // 添加到送货清单
        for (String hval : hvals) {
            cartInfoList.add(JSON.parseObject(hval, CartInfo.class));
        }
        jedis.close();
        return cartInfoList;
    }

    @Override
    public List<CartInfo> loadCartCache(String userId) {
        Jedis jedis = redisUtil.getJedis();
        // 定义Key    user:userId:cart
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        // SELECT * FROM cartInfo WHERE userId = ? 不可取，查询不到实时价格
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 数据库有数据，将数据库中的数据放入缓存
        if (cartInfoList != null && cartInfoList.size() > 0) {
            for (CartInfo cartInfo : cartInfoList) {
                jedis.hset(cartKey, cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
            }
            jedis.close();
        }
        return cartInfoList;
    }
}
