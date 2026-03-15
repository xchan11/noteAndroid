package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 支出分类占比，对应 GET /bill/chart/categoryRatio */
public class ChartCategoryRatioItem {

    @SerializedName("categoryName")
    public String categoryName;

    @SerializedName("totalAmount")
    public double totalAmount;

    @SerializedName("ratio")
    public double ratio;
}
