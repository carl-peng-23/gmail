package com.atecut.gmall.user.controller;

import com.atecut.gmall.bean.UserAddress;
import com.atecut.gmall.bean.UserInfo;
import com.atecut.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public List<UserInfo> findAll() {
        return userService.findAll();
    }

    @RequestMapping("/getUserAddressList")
    public List<UserAddress> getUserAddressList(@RequestParam("userId") String userId) {
        return userService.getUserAddressList(userId);
    }
}
