package com.mm.freedom.xposed;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.mm.freedom.utils.GLockUtils;
import com.mm.freedom.utils.GLogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.microedition.khronos.opengles.GL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed Activity Hook Extend Helper
 * 尽量不要持有 hook 对象,
 *
 * @param <T> 继承至Activity的子类
 * @author Gang, GitHub Home: https://github.com/GangJust
 */
public abstract class ActivityHelper<T extends Activity> extends XposedExtendHelper {

    protected ActivityHelper(@NonNull XC_LoadPackage.LoadPackageParam lpparam, @NonNull Class<T> targetClazz) {
        super(lpparam);
        hookOnCreate(targetClazz);
        hookOnStart(targetClazz);
        hookOnResume(targetClazz);
        hookOnPause(targetClazz);
        hookOnStop(targetClazz);
        hookOnRestart(targetClazz);
        hookOnDestroy(targetClazz);
        hookOnActivityResult(targetClazz);
        hookMore(lpparam, targetClazz);
    }

    // Hook onCrate
    private void hookOnCreate(Class<T> targetClazz) {
        Method onCreate = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onCreate", Bundle.class);
        if (onCreate == null) return;
        XposedBridge.hookMethod(onCreate, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeCreate(targetClazz.cast(param.thisObject), (Bundle) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterCreate(targetClazz.cast(param.thisObject), (Bundle) param.args[0]);
            }
        });
    }

    protected void onBeforeCreate(@NonNull T hookActivity, Bundle bundle) {
    }

    protected void onAfterCreate(@NonNull T hookActivity, Bundle bundle) {

    }

    // Hook onStart
    private void hookOnStart(Class<T> targetClazz) {
        Method onStart = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onStart");
        if (onStart == null) return;
        XposedBridge.hookMethod(onStart, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeStart(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterStart(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeStart(@NonNull T hookActivity) {

    }

    protected void onAfterStart(@NonNull T hookActivity) {

    }

    // Hook onResume
    private void hookOnResume(Class<T> targetClazz) {
        Method onResume = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onResume");
        if (onResume == null) return;
        XposedBridge.hookMethod(onResume, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeResume(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterResume(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeResume(@NonNull T hookActivity) {

    }

    protected void onAfterResume(@NonNull T hookActivity) {

    }

    // Hook onPause
    private void hookOnPause(Class<T> targetClazz) {
        Method onPause = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onPause");
        if (onPause == null) return;
        XposedBridge.hookMethod(onPause, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforePause(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterPause(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforePause(@NonNull T hookActivity) {

    }

    protected void onAfterPause(@NonNull T hookActivity) {

    }

    // Hook onStop
    private void hookOnStop(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onStop");
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeStop(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterStop(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeStop(@NonNull T hookActivity) {

    }

    protected void onAfterStop(@NonNull T hookActivity) {

    }

    // Hook onRestart
    private void hookOnRestart(Class<T> targetClazz) {
        Method onRestart = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onRestart");
        if (onRestart == null) return;
        XposedBridge.hookMethod(onRestart, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeRestart(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterRestart(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeRestart(@NonNull T hookActivity) {

    }

    protected void onAfterRestart(@NonNull T hookActivity) {

    }

    // Hook onDestroy
    private void hookOnDestroy(Class<T> targetClazz) {
        Method onDestroy = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onDestroy");
        if (onDestroy == null) return;
        XposedBridge.hookMethod(onDestroy, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeDestroy(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterDestroy(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeDestroy(@NonNull T hookActivity) {

    }

    protected void onAfterDestroy(@NonNull T hookActivity) {
    }

    // Hook onActivityResult, No tests
    private void hookOnActivityResult(Class<T> targetClazz) {
        Method onActivityResult = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onActivityResult", int.class, int.class, Intent.class);
        if (onActivityResult == null) return;
        XposedBridge.hookMethod(onActivityResult, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeActivityResult(targetClazz.cast(param.thisObject), (int) param.args[0], (int) param.args[1], (Intent) param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterActivityResult(targetClazz.cast(param.thisObject), (int) param.args[0], (int) param.args[1], (Intent) param.args[2]);
            }
        });
    }

    protected void onBeforeActivityResult(@NonNull T hookActivity, int requestCode, int resultCode, Intent data) {

    }

    protected void onAfterActivityResult(@NonNull T hookActivity, int requestCode, int resultCode, Intent data) {

    }

    //Hook More
    protected void hookMore(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {

    }

    //这个Application无法操作, UI相关, Toast都不行
    public Application getApplication() {
        return AndroidAppHelper.currentApplication();
    }
}
