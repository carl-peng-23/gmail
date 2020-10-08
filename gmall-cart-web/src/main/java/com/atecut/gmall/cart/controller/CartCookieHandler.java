package com.atecut.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atecut.gmall.bean.CartInfo;
import com.atecut.gmall.bean.SkuInfo;
import com.atecut.gmall.config.CookieUtil;
import com.atecut.gmall.service.ManageService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {

    // 定义购物车名称
    private String cookieCartName = "CART";

    // 设置cookie 过期时间
    private final int COOKIE_CART_MAXAGE = 7 * 24 * 3600;

    @Reference
    private ManageService manageService;

    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
        /*
            1、查看购物车中是否有该商品，数量相加/直接添加
         */
        // 从cookie中获取数据
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        boolean flag = false; // true购物车中有该商品，flase即没有
        List<CartInfo> cartInfoList = new ArrayList<>();
        // cookie中有购物车数据
        if (!StringUtils.isEmpty(cookieValue)) {
            // 将cartInfo集合转化成对象
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            // cookie中有购物车的数据
            for (CartInfo cartInfo : cartInfoList) {
                // 找到已存在要添加的商品
                if (skuId.equals(cartInfo.getSkuId())) {
                    // 修改数量
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                    // 实时价格初始化
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    // 将flag改为true，即cookie购物车中有该商品
                    flag = true;
                }
            }
        }
        // 不存在该商品
        if (!flag) {
            // 从数据库中查询商品详情，直接添加
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            // 添加到购物车
            cartInfoList.add(cartInfo);
        }
        String cartInfoJsonStr = JSON.toJSONString(cartInfoList);
        CookieUtil.setCookie(request, response, cookieCartName, cartInfoJsonStr, COOKIE_CART_MAXAGE, true);
    }

    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (!StringUtils.isEmpty(cookieValue)) {
            List<CartInfo> cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            return cartInfoList;
        }
        return null;
    }

    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, cookieCartName);
    }

    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        // 直接将isChecked值赋给Cookie中的购物车集合
        List<CartInfo> cartList = getCartList(request);
        if (cartList != null && cartList.size() > 0) {
            for (CartInfo cartInfo : cartList) {
                if (cartInfo.getSkuId().equals(skuId)) {
                    cartInfo.setIsChecked(isChecked);
                }
            }
        }
        // 写回Cookie
        CookieUtil.setCookie(request, response, cookieCartName, JSON.toJSONString(cartList), COOKIE_CART_MAXAGE, true);
    }
}
