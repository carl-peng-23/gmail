package com.atecut.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.SkuInfo;
import com.atecut.gmall.bean.SpuImage;
import com.atecut.gmall.bean.SpuSaleAttr;
import com.atecut.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class SkuManageController {

    @Reference
    ManageService manageService;

    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return manageService.getSpuImageList(spuImage);
    }

    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getspuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return  spuSaleAttrList;
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);;
    }
}
