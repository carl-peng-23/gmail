package com.atecut.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.CartInfo;
import com.atecut.gmall.bean.SkuInfo;
import com.atecut.gmall.config.LoginRequire;
import com.atecut.gmall.service.CartService;
import com.atecut.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private CartCookieHandler cartCookieHandler;

    @Reference
    private ManageService manageService;

    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        // 获取商品数量
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");
        if (userId != null) {
            // 调用登录添加购物车
            cartService.addToCart(skuId, userId, Integer.valueOf(skuNum));
        } else {
            // 调用未登录添加购物车
            cartCookieHandler.addToCart(request, response, skuId, userId, Integer.parseInt(skuNum));
        }
        // 根据skuId查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "success";
    }

    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        // 购物车列表
        List<CartInfo> cartInfoList = null;
        // 获取usrId
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            // 合并购物车
            List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
            if (cartListFromCookie != null && cartListFromCookie.size() > 0) {
                // 有数据在cookie中，合并
                cartInfoList = cartService.mergeToCartList(cartListFromCookie,userId);
                // 合并之后删除cookie中的购物车
                cartCookieHandler.deleteCartCookie(request, response);
            } else {
                // 调用登录添加购物车
                cartInfoList = cartService.getCartList(userId);
            }
        } else {
            // 调用未登录添加购物车
            cartInfoList = cartCookieHandler.getCartList(request);
        }
        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }

    @RequestMapping("checkCart")
    @ResponseBody
    @LoginRequire(autoRedirect = false)
    public void checkCart(HttpServletRequest request, HttpServletResponse response) {
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");

        if (userId != null) {
            // 登录状态
            cartService.checkCart(skuId,isChecked,userId);
        } else {
            // 未登录状态
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }

    @RequestMapping("toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response) {
        // 合并勾选的商品
        List<CartInfo> cartList = cartCookieHandler.getCartList(request);
        if (cartList != null && cartList.size() > 0) {
            // 合并
            cartService.mergeToCartList(cartList, (String)request.getAttribute("userId"));
            // 合并之后删除cookie中的购物车
            cartCookieHandler.deleteCartCookie(request, response);
        }
        return "redirect://order.gmall.com/trade";
    }
}
