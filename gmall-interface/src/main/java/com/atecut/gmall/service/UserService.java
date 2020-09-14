package com.atecut.gmall.service;


import com.atecut.gmall.bean.UserAddress;
import com.atecut.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> findAll();

    List<UserAddress> getUserAddressList(String userId);
}
