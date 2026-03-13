package com.example.note.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 日程实体（后端 Note）。
 * status: 0=待办，1=已完成
 * priority: 1/2/3
 */
public class Note implements Serializable {
    @SerializedName("noteId")
    public int noteId;
    @SerializedName("title")
    public String title;
    @SerializedName("content")
    public String content;
    @SerializedName("planTime")
    public long planTime;
    @SerializedName("priority")
    public int priority;
    @SerializedName("remindTime")
    public Long remindTime;
    @SerializedName("status")
    public int status;
}

