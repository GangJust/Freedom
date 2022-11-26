package com.mm.freedom.utils;

import android.content.Context;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;

public class GLogUtils {
    public static final String X_LOG_TAG = "xLog - ";
    public static final String X_Exception_TAG = "xException - ";

    public static void xLog(Object log) {
        XposedBridge.log(X_LOG_TAG + "\n" + log);
    }

    public static void xLogAndToast(Context context, Object log) {
        xLog(log);
        Toast makeText = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        makeText.setText(String.valueOf(log));
        makeText.show();
    }

    public static void xException(Object log) {
        XposedBridge.log(X_Exception_TAG + log);
    }
}
