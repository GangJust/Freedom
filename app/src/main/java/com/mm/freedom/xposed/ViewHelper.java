package com.mm.freedom.xposed;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed View Hook Extend Helper
 * 尽量不要持有 hook 对象, 该类没有经过测试
 *
 * @param <T> 继承至View的子类
 * @author Gang, GitHub Home: https://github.com/GangJust
 */
public class ViewHelper<T extends View> extends XposedExtendHelper {
    protected ViewHelper(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {
        super(lpparam);
        hookConstructor1(targetClazz);
        hookConstructor2(targetClazz);
        hookConstructor3(targetClazz);
        hookConstructor4(targetClazz);
        hookMore(lpparam, targetClazz);
    }

    // Hook constructor 1
    private void hookConstructor1(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor1(targetClazz.cast(param.thisObject), (Context) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor1(targetClazz.cast(param.thisObject), (Context) param.args[0]);
            }
        });
    }

    protected void onBeforeConstructor1(@NonNull T hookView, Context context) {

    }

    protected void onAfterConstructor1(@NonNull T hookView, Context context) {

    }

    // Hook constructor 2
    private void hookConstructor2(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class, AttributeSet.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor2(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor2(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1]);
            }
        });
    }

    protected void onBeforeConstructor2(@NonNull T hookView, Context context, @Nullable AttributeSet attrs) {

    }

    protected void onAfterConstructor2(@NonNull T hookView, Context context, @Nullable AttributeSet attrs) {

    }

    // Hook constructor 3
    private void hookConstructor3(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class, AttributeSet.class, int.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor3(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (int) param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor3(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (int) param.args[2]);
            }
        });
    }

    protected void onBeforeConstructor3(@NonNull T hookView, Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

    }

    protected void onAfterConstructor3(@NonNull T hookView, Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

    }

    // Hook constructor 4
    private void hookConstructor4(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class, AttributeSet.class, int.class, int.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor4(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (int) param.args[2], (int) param.args[3]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor4(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (int) param.args[2], (int) param.args[3]);
            }
        });
    }

    protected void onBeforeConstructor4(@NonNull T hookView, Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRe) {

    }

    protected void onAfterConstructor4(@NonNull T hookView, Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRe) {

    }


    // Hook More
    protected void hookMore(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {

    }
}
