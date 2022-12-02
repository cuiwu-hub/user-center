package com.cxy.usercenter.service;

import com.cxy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    User getSafetyUser(User user);
    int userLogout(HttpServletRequest request);
}
