package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 收支记录，对应 /bill/record/*。type: 1=收入，2=支出 */
public class BillRecord implements Serializable {

    @SerializedName("recordId")
    public long recordId;

    @SerializedName("categoryId")
    public long categoryId;

    @SerializedName("categoryName")
    public String categoryName;

    @SerializedName("type")
    public int type;

    @SerializedName("amount")
    public double amount;

    @SerializedName("remark")
    public String remark;

    @SerializedName("createTime")
    public String createTime;
}
