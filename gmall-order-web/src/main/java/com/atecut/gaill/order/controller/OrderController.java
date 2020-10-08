package com.atecut.gaill.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.*;
import com.atecut.gmall.bean.enums.OrderStatus;
import com.atecut.gmall.bean.enums.ProcessStatus;
import com.atecut.gmall.config.LoginRequire;
import com.atecut.gmall.service.CartService;
import com.atecut.gmall.service.ManageService;
import com.atecut.gmall.service.OrderService;
import com.atecut.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class OrderController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    @Reference
    private ManageService manageService;

    @RequestMapping("trade")
    @LoginRequire(autoRedirect = true)
    public String trade(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        request.setAttribute("userAddressList", userAddressList);

        // 展示送货清单   user:userId:isChecked
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);
        // 订单信息集合
        List<OrderDetail> orderDetailList = new ArrayList<>(cartInfoList.size());
        // 将集合数据赋值给orderDetail
        for (CartInfo cartInfo : cartInfoList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetailList.add(orderDetail);
        }
        // 总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        request.setAttribute("totalAmount", orderInfo.getTotalAmount());
        request.setAttribute("orderDetailList", orderDetailList);

        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo", tradeNo);
        return "trade";
    }

    @RequestMapping(value = "submitOrder", method = RequestMethod.POST)
    @LoginRequire(autoRedirect = true)
    public String submitOrder(OrderInfo orderInfo, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        // 判断是否是重复提交
        String tradeNo = (String) request.getParameter("tradeNo");
        // 调用比较方法
        boolean result = orderService.checkTradeCode(userId, tradeNo);
        if (!result) {
            // 比较失败
            request.setAttribute("errMsg", "请勿重复提交");
            return "tradeFail";
        }
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (orderDetailList != null && orderDetailList.size() > 0) {
            for (OrderDetail orderDetail : orderDetailList) {
                // 验证库存
                boolean checkStock = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
                if (!checkStock) {
                    // 库存不足
                    request.setAttribute("errMsg",  orderDetail.getSkuName() + "库存不足");
                    return "tradeFail";
                }
                // 验证金额是否变化
                SkuInfo skuInfo = manageService.getSkuInfo(orderDetail.getSkuId());
                int checkPrice = skuInfo.getPrice().compareTo(orderDetail.getOrderPrice());
                if (checkPrice != 0) {
                    // 金额不匹配
                    // 修改缓存
                    cartService.loadCartCache(userId);
                    // 错误信息
                    request.setAttribute("errMsg",  orderDetail.getSkuName() + "金额不匹配");
                    return "tradeFail";
                }
            }
        }
        // 初始化参数
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.sumTotalAmount();
        orderInfo.setUserId(userId);
        // 设置创建时间
        orderInfo.setCreateTime(new Date());
        // 设置失效时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());
        // 生成第三方支付编号
        String outTradeNo="ATECUT"+System.currentTimeMillis()+""+new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        // 保存
        String orderId = orderService.saveOrder(orderInfo);
        // 删除redis中流水号
        orderService.delTradeCode(userId);
        // 重定向
        return "redirect://payment.gmall.com/index?orderId=" + orderId;
    }
}
