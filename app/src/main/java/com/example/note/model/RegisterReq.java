package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 用户注册请求体 */
public class RegisterReq {
    @SerializedName("username")
    public String username;
    @SerializedName("phone")
    public String phone;
    @SerializedName("password")
    public String password;
    @SerializedName("confirmPassword")
    public String confirmPassword;

    public RegisterReq(String username, String phone, String password, String confirmPassword) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
