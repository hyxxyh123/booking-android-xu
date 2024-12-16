package com.my.myapplication.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.global.Config;

public class ImageUtils {
    public static void displayImage(Context context, ImageView imageView, String str) {
        if (!"".equals(str)) {
            try {
                //加载图片
                Glide.with(context)
                        .load(Config.HTTP_BASE_URL + str)
                        .error(R.mipmap.zuowei)
                        .into(imageView);
            } catch (Exception e) {
                //图片显示错误处理
                imageView.setImageResource(R.mipmap.zuowei);
            }
        } else {
            imageView.setImageResource(R.mipmap.zuowei);
        }
    }
}
