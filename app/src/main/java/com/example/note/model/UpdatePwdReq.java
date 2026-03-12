package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 修改密码请求体 */
public class UpdatePwdReq {
    @SerializedName("oldPwd")
    public String oldPwd;
    @SerializedName("newPwd")
    public String newPwd;
    @SerializedName("confirmPwd")
    public String confirmPwd;

    public UpdatePwdReq(String oldPwd, String newPwd, String confirmPwd) {
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
        this.confirmPwd = confirmPwd;
    }
}
