package com.mm.freedom;

import android.app.Application;
import android.os.Bundle;

import com.mm.freedom.activity.MainActivity;
import com.mm.freedom.hook.douyin.HookDY;
import com.mm.freedom.utils.GLogUtils;
import com.ss.android.ugc.aweme.app.host.AwemeHostApplication;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // do`t write the same hook logic in two methods at the same time, and do not call each other in the same way.
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam, Application application) throws Throwable {
        if (Arrays.asList(PackNames.packNames).contains(lpparam.packageName)) {
            if (PackNames.MINE_PACK.equals(lpparam.packageName)) {
                initModuleHookSuccess(lpparam);
            } else {
                hookApp(lpparam, application);
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
    private void hookApp(XC_LoadPackage.LoadPackageParam lpparam, Application application) {
        //GLogUtil.xLog(lpparam.packageName + " Hook!");
        GLogUtils.xLog(application.getClass().getName());
        if (application instanceof AwemeHostApplication) {
            new HookDY(lpparam);
        }
    }
}
