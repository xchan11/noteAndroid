package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 用户登录请求体 */
public class LoginReq {
    @SerializedName("phone")
    public String phone;
    @SerializedName("password")
    public String password;

    public LoginReq(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
