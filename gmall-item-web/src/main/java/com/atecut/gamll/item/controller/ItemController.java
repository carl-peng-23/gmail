package com.atecut.gamll.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atecut.gmall.bean.SkuInfo;
import com.atecut.gmall.bean.SkuSaleAttrValue;
import com.atecut.gmall.bean.SpuSaleAttr;
import com.atecut.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    ManageService manageService;

    // @LoginRequire
    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable("skuId") String skuId, Model model) {
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
//        List<SkuImage> skuImageList = manageService.getSkuImageBySkuId(skuId);
//        skuInfo.setSkuImageList(skuImageList);
        model.addAttribute("skuInfo", skuInfo);

        // 查询销售属性，销售属性值集合
        List<SpuSaleAttr> saleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        model.addAttribute("saleAttrList", saleAttrList);

        // 获取销售属性值id
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        String valueIdsKey = "";

        Map<String, String> valuesSkuMap = new HashMap<>();

        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if (valueIdsKey.length() != 0) {
                valueIdsKey = valueIdsKey + "|";
            }
            valueIdsKey = valueIdsKey + skuSaleAttrValue.getSaleAttrValueId();

            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {

                valuesSkuMap.put(valueIdsKey, skuSaleAttrValue.getSkuId());
                valueIdsKey = "";
            }

        }

        // 把map变成json串
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);
        System.out.println(valuesSkuJson);

        model.addAttribute("valuesSkuJson", valuesSkuJson);

        return "item";
    }
}
