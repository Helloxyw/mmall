package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * check the count of username
     */
    int checkUsername(String username);

    /**
     * check the email
     * @param email
     * @return
     */
    int checkEmail(String email);

    /**
     * check login
     */
    User selectLogin(@Param("username") String username, @Param("password") String password);
}