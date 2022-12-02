package com.cxy.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {
    private String userAccountRegister;
    private String userPasswordRegister;
    private String checkPasswordRegister;
    private String planetCodeRegister;
}
