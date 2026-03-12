package com.example.note.manager;

import com.example.note.BuildConfig;

public class GlobalConfig {

    /**
     * 当前是否属于debug状态
     * */
    public static boolean isDebug(){
        //return false;
        return BuildConfig.DEBUG;
    }

    /**
     * 当前是否属于debug状态
     * */
    public static boolean showApiError(){
        return false&&isDebug();
        //return BuildConfig.debug;
    }
}
