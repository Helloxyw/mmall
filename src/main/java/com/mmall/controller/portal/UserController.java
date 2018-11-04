package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author root
 * @Date 18-9-1 下午7:34
 * @Version 1.0
 **/
@Controller
@RequestMapping("/user/")
public class UserController {

    /**
     * login
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        return null;
    }
}
