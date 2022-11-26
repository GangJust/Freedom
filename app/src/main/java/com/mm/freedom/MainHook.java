package com.mm.freedom;

import android.os.Bundle;

import com.mm.freedom.activity.MainActivity;
import com.mm.freedom.hook.douyin.HookDY;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Arrays.asList(PackNames.packNames).contains(lpparam.packageName)) {
            if (PackNames.MINE_PACK.equals(lpparam.packageName)) {
                initModuleHookSuccess(lpparam);
            } else {
                hookApp(lpparam);
            }
        }
    }

    //Hook模块本身
    private void initModuleHookSuccess(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //GLogUtil.xLog("Freedom Module Hook!");
        //模块加载成功, 改变模块状态
        XposedHelpers.findAndHookMethod(
                MainActivity.class.getName(),
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object object = param.thisObject;
                        Method method = object.getClass().getDeclaredMethod("moduleHookSuccessText");
                        method.setAccessible(true);
                        method.invoke(object);
                    }
                });
    }

    //Hook目标应用
    private void hookApp(XC_LoadPackage.LoadPackageParam lpparam) {
        //GLogUtil.xLog(lpparam.packageName + " Hook!");
        new HookDY(lpparam);
    }
}
