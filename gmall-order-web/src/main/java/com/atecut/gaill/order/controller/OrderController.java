package com.atecut.gaill.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.UserAddress;
import com.atecut.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class OrderController {

    @Reference
    private UserService userService;
//    @RequestMapping("trade")
//    public String trade() {
//        return "index";
//    }
    @RequestMapping("trade")
    @ResponseBody
    public List<UserAddress> trade(String userId) {
        System.out.println("=======orderController===>trade()========");
        return userService.getUserAddressList(userId);
    }
}
