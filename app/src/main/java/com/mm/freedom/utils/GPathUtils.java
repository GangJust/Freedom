package com.mm.freedom.utils;

import android.content.Context;

import java.io.File;
import java.util.Objects;

public class GPathUtils {

    /**
     * 获取外部存储器路径
     *
     * @param context context
     * @return 外部存储器路径
     */
    public static String getStoragePath(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        do {
            externalFilesDir = Objects.requireNonNull(externalFilesDir).getParentFile();
        } while (Objects.requireNonNull(externalFilesDir).getAbsolutePath().contains("/Android"));

        return Objects.requireNonNull(externalFilesDir).getAbsolutePath();
    }

    /**
     * 替换可能造成文件失败的特殊字符
     *
     * @return 被替换后的字符
     */
    public static String replaceSpecialChars(String name) {
        return name.replaceAll("\\s", "")
                .replaceAll("<", "《")
                .replaceAll(">", "》")
                .replaceAll(":", "-")
                .replaceAll("\"", "”")
                .replaceAll("/", "-")
                .replaceAll("\\\\", "-")
                .replaceAll("\\|", "-")
                .replaceAll("\\?", "？")
                .replaceAll("\\*", "-");
    }
}
