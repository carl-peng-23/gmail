package com.atecut.gmall.service;

import com.atecut.gmall.bean.SkuLsInfo;
import com.atecut.gmall.bean.SkuLsParams;
import com.atecut.gmall.bean.SkuLsResult;

public interface ListService {
    public void saveSkuInfo(SkuLsInfo skuLsInfo);
    public SkuLsResult search(SkuLsParams skuLsParams);
}
