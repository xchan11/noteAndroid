package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 记账分类，对应 /bill/category/* */
public class BillCategory implements Serializable {

    @SerializedName("categoryId")
    public long categoryId;

    @SerializedName("categoryName")
    public String categoryName;
}
