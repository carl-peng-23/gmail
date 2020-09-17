package com.atecut.gmall.service.mapper;

import com.atecut.gmall.bean.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
