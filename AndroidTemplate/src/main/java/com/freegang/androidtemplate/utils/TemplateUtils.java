package com.freegang.androidtemplate.utils;

import android.content.Context;

public class TemplateUtils {

    private TemplateUtils() {
        ///
    }

    //尺寸工具类
    public static class SizeUtil {
        private SizeUtil() {
            ///
        }

        public static int dp2Px(Context context, int dp) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        public static int px2Dp(Context context, int px) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (px / scale + 0.5f);
        }
    }
}
