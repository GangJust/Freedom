package com.mm.freedom.utils;

import android.content.Context;

import java.io.File;
import java.util.Objects;

public class GPathUtils {

    /**
     * 获取外部存储器路径
     *
     * @param context
     * @return
     */
    public static String getStoragePath(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        do {
            externalFilesDir = Objects.requireNonNull(externalFilesDir).getParentFile();
        } while (Objects.requireNonNull(externalFilesDir).getAbsolutePath().contains("/Android"));

        return Objects.requireNonNull(externalFilesDir).getAbsolutePath();
    }
}
