package com.cxy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cxy.usercenter.common.BaseResponse;
import com.cxy.usercenter.common.ErrorCode;
import com.cxy.usercenter.common.ResultUtils;
import com.cxy.usercenter.constant.UserConstant;
import com.cxy.usercenter.model.domain.User;
import com.cxy.usercenter.model.domain.request.UserLoginRequest;
import com.cxy.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.cxy.usercenter.model.domain.request.UserRegisterRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController implements UserConstant {
    @Resource
    private UserService userService;
    //注册用户
    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccountRegister();
        String userPassword = userRegisterRequest.getUserPasswordRegister();
        String checkPassword = userRegisterRequest.getCheckPasswordRegister();
        String planetCode = userRegisterRequest.getPlanetCodeRegister();
        //判断参数是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "字段不能为空");
        }
        long result =  userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    //用户登陆
    @PostMapping("/login")
    public BaseResponse<User> UserLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    //获取用户登陆态
    @GetMapping("/currentUser")
    public BaseResponse<User> getUserCurrent(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser != null) {
            long userId = currentUser.getId();
            User user =  userService.getById(userId);
            User safetyUser = userService.getSafetyUser(user);
            return ResultUtils.success(safetyUser);
        }
        return ResultUtils.error(ErrorCode.NOT_LOGIN);
    }

    //用户查询
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //仅管理员可查询
        if (!isAdmin(request)) {
            return ResultUtils.error(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            wrapper.like("username", username);
        }
        List<User> userList = userService.list(wrapper);
        //将密码设置为null并生成新的list
        List result = userList.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    //删除用户
    @PostMapping("/delete")
    public BaseResponse userDelete(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResultUtils.error(ErrorCode.NO_AUTH);
        }
        if (id < 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(id));
    }

    //退出登陆
    @PostMapping("/outLogin")
    public BaseResponse userLogout(HttpServletRequest request) {
        if (request == null) ResultUtils.error(ErrorCode.NULL_ERROR);
        int count = userService.userLogout(request);
        return ResultUtils.success(count);
    }
    //判断是否是管理员
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user == null || user.getUserRole().equals(ADMIN_ROLE);
    }
}
