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
     *
     * @param email
     * @return
     */
    int checkEmail(String email);

    /**
     * checkLogin
     *
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username") String username, @Param("password") String password);

    /**
     * select question by username
     *
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * check the answer
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(@Param("username") String username, @Param("question") String question,
                    @Param("answer") String answer);

    /**
     * update password
     *
     * @param username
     * @param passwordNew
     * @return
     */
    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);


    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);
}