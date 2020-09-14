package com.atecut.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atecut.gmall.bean.UserAddress;
import com.atecut.gmall.bean.UserInfo;
import com.atecut.gmall.service.UserService;
import com.atecut.gmall.user.mapper.UserAddressMapper;
import com.atecut.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        Example example = new Example(UserAddress.class);
        example.createCriteria().andEqualTo("userId", userId);
        return userAddressMapper.selectByExample(example);
    }
}
