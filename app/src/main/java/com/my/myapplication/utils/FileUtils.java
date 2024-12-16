package com.my.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    //    选择图片大小限制
    public static final int IMAGE_MAX_SIZE = 1024 * 100;

    //或者文件大小 拍照或者选择照片回调时候用到
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return size;
    }


    /**
     * 转换
     */
    public static String getPath(String path) {

        return path;
    }

    /**
     * 得到图片
     */
    public static Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);

    }

    public static String readTxt(String path) {
        try {

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            // 创建BufferedReader对象来读取文件内容
            Reader InputStreamReader;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            String result = "";
            // 逐行读取文件内容并打印
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // 关闭流
            bufferedReader.close();
            inputStreamReader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 判断文件字符编码
     */
    public static String charset(String path) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 > read || read > 0xBF) {
                            break; // 双字节 (0xC0 - 0xDF),(0x80 - 0xBF),也可能在GB编码内
                        }
                    } else if (0xE0 <= read) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                            }
                        }
                        break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charset;
    }
}
