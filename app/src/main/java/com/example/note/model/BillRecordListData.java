package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** 按分类查询收支记录返回体 data：{ total, list } */
public class BillRecordListData {

    @SerializedName("total")
    public long total;

    @SerializedName("list")
    public List<BillRecord> list;
}
