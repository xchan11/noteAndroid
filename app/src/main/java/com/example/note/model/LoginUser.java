package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/**
 * 登录/注册成功时 data 结构。接口不返回密码。
 */
public class LoginUser {
    @SerializedName("userId")
    public long userId;
    @SerializedName("username")
    public String username;
    @SerializedName("phone")
    public String phone;
}
