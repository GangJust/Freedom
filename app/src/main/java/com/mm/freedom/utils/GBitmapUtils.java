package com.mm.freedom.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class GBitmapUtils {
    /**
     * 将bitmap转换为本地的图片
     *
     * @param bitmap
     * @return
     */
    public static boolean bitmap2Path(Bitmap bitmap, String path, String filename) {
        try {
            OutputStream output = new FileOutputStream(new File(path, filename));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            GLogUtils.xException(e.getMessage());
        }
        return false;
    }
}
