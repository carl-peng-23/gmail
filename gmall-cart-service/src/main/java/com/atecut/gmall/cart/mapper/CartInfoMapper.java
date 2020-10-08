package com.atecut.gmall.cart.mapper;

import com.atecut.gmall.bean.CartInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CartInfoMapper extends Mapper<CartInfo> {

    List<CartInfo> selectCartListWithCurPrice(@Param("userId") String userId);
}
