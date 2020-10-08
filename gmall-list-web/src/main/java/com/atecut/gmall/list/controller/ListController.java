package com.atecut.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atecut.gmall.bean.SkuLsParams;
import com.atecut.gmall.bean.SkuLsResult;
import com.atecut.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @RequestMapping("list.html")
    @ResponseBody
    public String getList(SkuLsParams skuLsParams) {
        SkuLsResult search = listService.search(skuLsParams);
        return JSON.toJSONString(search);
    }
}