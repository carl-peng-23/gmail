package com.atecut.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atecut.gmall.bean.UserAddress;
import com.atecut.gmall.bean.UserInfo;
import com.atecut.gmall.config.RedisUtil;
import com.atecut.gmall.service.UserService;
import com.atecut.gmall.user.mapper.UserAddressMapper;
import com.atecut.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String userKey_prefix = "user:";

    public String userinfoKey_suffix = ":info";

    public int userKey_timeOut = 60 * 60 * 24;

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

    /**
     * 登录方法
     *
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        String passwd = userInfo.getPasswd();
        // 密码加密
        String pwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(pwd);
        UserInfo info = userInfoMapper.selectOne(userInfo);
        // System.out.println("账号:" + userInfo.getLoginName() + "\t密码:" +userInfo.getPasswd() + "\t" + info);
        System.out.println(info);
        if (info != null) {
            System.out.println("redisUtil" + redisUtil);
            Jedis jedis = redisUtil.getJedis();
            String userKey = userKey_prefix + info.getId() + userinfoKey_suffix;
            jedis.setex(userKey, userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String userKey = userKey_prefix + userId + userinfoKey_suffix;
        String userJsonStr = jedis.get(userKey);
        if(!StringUtils.isEmpty(userJsonStr)) {
            UserInfo userInfo = JSON.parseObject(userJsonStr, UserInfo.class);
            jedis.close();
            return userInfo;
        }
        return null;
    }
}
