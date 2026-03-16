package com.example.note.model;

import com.google.gson.annotations.SerializedName;

/** 月度预算信息，对应 GET /bill/budget/get */
public class BudgetInfo {

    @SerializedName("yearMonth")
    public String yearMonth;

    @SerializedName("budgetAmount")
    public double budgetAmount;

    @SerializedName("totalSpend")
    public double totalSpend;

    @SerializedName("remainAmount")
    public double remainAmount;

    @SerializedName("isOverspend")
    public boolean isOverspend;

    @SerializedName("overspendAmount")
    public double overspendAmount;

    @SerializedName("count")
    public Long count; // 复用统计接口时的总数（如 goodsTotal/billTotal/noteTodo）
}
