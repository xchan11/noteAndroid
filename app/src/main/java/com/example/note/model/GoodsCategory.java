package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 物品分类实体，对应 /category/* 接口。
 */
public class GoodsCategory implements Serializable {

    @SerializedName("categoryId")
    public long categoryId;

    @SerializedName("categoryName")
    public String categoryName;

    @SerializedName("createTime")
    public Long createTime;
}

