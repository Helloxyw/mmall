package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("the password error");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login success", user);

    }

    public ServerResponse<String> register(User user) {

        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);

        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5 encrypt
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);

        if (0 == resultCount) {
            return ServerResponse.createByErrorMessage("register fail");
        }

        return ServerResponse.createBySuccessMessage("register success");
    }


    public ServerResponse<String> checkValid(String str, String type) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(type)) {
            //start check
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("the username has exist!");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("the emial has exist!");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("the param is error");
        }
        return ServerResponse.createBySuccessMessage("check success");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //the user isn't exist
            return ServerResponse.createByErrorMessage("the user is not exist");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("the select question is empty");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //means the qestion and the answer belong to user and they are correct
            String forgetToken = UUID.randomUUID().toString();

        }
        return null;
    }
}
