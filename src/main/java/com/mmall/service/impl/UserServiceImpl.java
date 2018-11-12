package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
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
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("the answer is wrong");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("the param is wrong,token need delive");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //the user isn't exist
            return ServerResponse.createByErrorMessage("the user is not exist");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token is invalid or out of date");
        }

        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("update password success");
            }
        } else {
            return ServerResponse.createByErrorMessage("token is wrong,please get the token again");
        }

        return ServerResponse.createByErrorMessage("update password fail");
    }


    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {

        int resuleCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        return null;
    }


    public ServerResponse<User> updateInformation(User user) {
        //username cannot be updated
        //emial need a check to ensure  if the new email exist, and new email
        //has exist ,it cannot belong to current user

        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("the email has exist,please change" +
                    "the new email and try again");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("update user information success", updateUser);
        }
        return ServerResponse.createByErrorMessage("update user information failed");
    }


    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return ServerResponse.createByErrorMessage("can not find the user");
        }

        //hide password
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend

    /**
     * check is Admin
     *
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
