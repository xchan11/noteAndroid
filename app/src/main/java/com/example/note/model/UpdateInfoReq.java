package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 修改基本信息请求体（APP 全量传参） */
public class UpdateInfoReq {
    @SerializedName("username")
    public String username;
    @SerializedName("phone")
    public String phone;

    public UpdateInfoReq(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }
}
