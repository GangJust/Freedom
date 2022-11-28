package com.mm.freedom.xposed;


import android.app.AndroidAppHelper;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed Hook Extend Helper
 * 尽量不要持有 hook 对象,
 *
 * @author Gang, GitHub Home: https://github.com/GangJust
 */
public abstract class XposedExtendHelper {
    protected static final Application application = AndroidAppHelper.currentApplication();
    protected static final Handler handler = new Handler(Looper.getMainLooper()); //创建一个静态消息队列, 最大限度缓解内存泄漏
    protected final XC_LoadPackage.LoadPackageParam lpparam;

    protected XposedExtendHelper(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;
        hookMore(lpparam);
    }

    protected void findMethodAndHookIfExists(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        ArrayList<Object> list = new ArrayList<>(Arrays.asList(parameterTypesAndCallback));
        XC_MethodHook xcMethodHook = null;
        if (list.get(list.size() - 1) instanceof XC_MethodHook) {
            xcMethodHook = (XC_MethodHook) list.remove(list.size() - 1);
        }
        Method method = XposedHelpers.findMethodExactIfExists(className, classLoader, methodName, list.toArray());
        if (method == null) return;
        if (xcMethodHook == null) return;
        XposedBridge.hookMethod(method, xcMethodHook);
    }

    protected void hookMore(XC_LoadPackage.LoadPackageParam lpparam) {

    }

}
