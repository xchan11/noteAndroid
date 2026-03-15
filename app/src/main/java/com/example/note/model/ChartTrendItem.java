package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 趋势图单日数据，对应 GET /bill/chart/trend */
public class ChartTrendItem {

    @SerializedName("date")
    public String date;

    @SerializedName("income")
    public double income;

    @SerializedName("spend")
    public double spend;
}
