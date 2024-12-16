package com.my.myapplication.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;


public class ScreenUtil {

    //获取屏幕宽度
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

}
