package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 物品实体，对应 /goods/* 接口。
 *
 * 注意：
 * - shelfLife / openDate / remindXd 可能为 null
 * - isExpire: 0 未过期，1 已过期
 */
public class GoodsInfo implements Serializable {

    @SerializedName("goodsId")
    public long goodsId;

    @SerializedName("categoryId")
    public long categoryId;

    @SerializedName("goodsName")
    public String goodsName;

    @SerializedName("shelfLife")
    public Long shelfLife;

    @SerializedName("openDate")
    public Long openDate;

    @SerializedName("traceInfo")
    public String traceInfo;

    @SerializedName("remind7d")
    public Long remind7d;

    @SerializedName("remind3d")
    public Long remind3d;

    @SerializedName("remind1d")
    public Long remind1d;

    @SerializedName("isExpire")
    public int isExpire;
}

