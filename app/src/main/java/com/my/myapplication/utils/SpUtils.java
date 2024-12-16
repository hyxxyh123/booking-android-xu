package com.my.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    private static SpUtils instance;
    private SharedPreferences sharedPreferences;

    public SpUtils(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        }
    }

    public static SpUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SpUtils(context);
        }
        return instance;
    }

    /* 存储字符串 */
    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /* 获取字符串，默认值为 "" */
    public String getString(String key) {
        return getString(key, "");
    }

    /* 获取字符串，指定默认值 */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /* 存储 int 类型数据 */
    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /* 获取 int 类型数据，默认值为 0 */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /* 获取 int 类型数据，指定默认值 */
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    /* 存储 boolean 类型数据 */
    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /* 获取 boolean 类型数据，默认值为 false */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /* 获取 boolean 类型数据，指定默认值 */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /* 存储浮点数类型数据 */
    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    /* 获取浮点数类型数据，默认值为 0.0 */
    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    /* 获取浮点数类型数据，指定默认值 */
    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /* 存储长整型类型数据 */
    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    /* 获取长整型数据，默认值为 0 */
    public long getLong(String key) {
        return getLong(key, 0);
    }

    /* 获取长整型数据，指定默认值 */
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    /* 移除指定 key 的数据 */
    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    /* 清空所有数据 */
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}