package com.atecut.gmall.service.mapper;

import com.atecut.gmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSpuSaleAttrList(@Param("spu_id") String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("id") String id, @Param("spuId") String spuId);
}
