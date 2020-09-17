package com.atecut.gmall.service.mapper;

import com.atecut.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(@Param("catalog3Id") String catalog3Id);
}