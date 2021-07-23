package com.lyl.facerecognition.Utils;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class BitmapUtil {

    /**
     * 保存Bitmap为文件;可能报空指针是因为没有配置权限
     *
     * @param bmp
     * @param filename
     * @return
     */

    public static void saveBitmap2file(Bitmap bmp, String filename, Context context) {
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {

            stream = new FileOutputStream(context.getExternalFilesDir(null).getPath()
                    + "/"
                    + filename
                    + ".jpg");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        try {
            bmp.compress(format, quality, stream);
            stream.flush();
            stream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件为Bitmap
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapFromFile(String filename, Context context) {
        try
        {
            return BitmapFactory.decodeStream(new FileInputStream(context.getExternalFilesDir(null).getPath()
                    + "/"
                    + filename
                    + ".jpg"));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

