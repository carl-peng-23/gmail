package com.atecut.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.UserInfo;
import com.atecut.gmall.passport.config.JwtUtil;
import com.atecut.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @Value("${token.key}")
    private String signKey;

    @RequestMapping("index")
    public String index(ServletRequest request) {
        // 保存originUrl
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UserInfo userInfo) {
        // 取得ip地址
        String remoteAddr = request.getHeader("X-forwarded-for");
        if (userInfo != null) {
            UserInfo loginUser = userService.login(userInfo);
            if (loginUser == null) {
                return "fail";
            } else {
                // 生成token
                Map map = new HashMap();
                map.put("userId", loginUser.getId());
                map.put("nickName", loginUser.getNickName());
                String token = JwtUtil.encode(signKey, map, remoteAddr);
                return token;
            }
        }
        return "fail";
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
//        String salt = request.getHeader("X-forwarded-for");
        // 获取token
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
        // 调用jwt工具类
        Map<String, Object> map = JwtUtil.decode(token, signKey, salt);
        if (map != null && map.size() > 0) {
            String userId = (String) map.get("userId");
            UserInfo userInfo = userService.verify(userId);
            if (userInfo!=null){
                return "success";
            }
        }
        return "fail";
    }
}
