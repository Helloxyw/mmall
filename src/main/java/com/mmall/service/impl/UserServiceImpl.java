package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author root
 * @Date 18-9-5 上午1:01
 * @Version 1.0
 **/

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (0 == resultCount) {
            return ServerResponse.createByErrorMessage("the username is not exist!");
        }

        //todo password login by MD5
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("the password error");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login success", user);

    }
}
