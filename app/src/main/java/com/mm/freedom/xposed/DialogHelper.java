package com.mm.freedom.xposed;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed Dialog Hook Extend Helper
 * 尽量不要持有 hook 对象
 * <p>
 * 需要其他方法自行添加, 写起来可太累了
 *
 * @param <T> 继承至Dialog的子类
 * @author Gang, GitHub Home: https://github.com/GangJust
 */
public class DialogHelper<T extends Dialog> extends XposedExtendHelper {
    protected DialogHelper(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {
        super(lpparam);
        hookConstructor1(targetClazz);
        hookConstructor2(targetClazz);
        hookConstructor3(targetClazz);

        hookOnCreate(targetClazz);
        hookOnStart(targetClazz);
        hookOnStop(targetClazz);

        hookCreate(targetClazz);
        hookShow(targetClazz);
        hookHide(targetClazz);
        hookDismiss(targetClazz);

        hookSetContentView1(targetClazz);
        hookSetContentView2(targetClazz);
        hookSetContentView3(targetClazz);

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

    protected void onBeforeConstructor1(@NonNull T hookDialog, Context context) {

    }

    protected void onAfterConstructor1(@NonNull T hookDialog, Context context) {

    }

    // Hook constructor 2
    private void hookConstructor2(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class, int.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor2(targetClazz.cast(param.thisObject), (Context) param.args[0], (int) param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor2(targetClazz.cast(param.thisObject), (Context) param.args[0], (int) param.args[1]);
            }
        });
    }

    protected void onBeforeConstructor2(@NonNull T hookDialog, Context context, int themeResId) {

    }

    protected void onAfterConstructor2(@NonNull T hookDialog, Context context, int themeResId) {

    }

    // Hook constructor 3
    private void hookConstructor3(Class<T> targetClazz) {
        Constructor<?> constructor = XposedHelpers.findConstructorExactIfExists(targetClazz.getName(), lpparam.classLoader, Context.class, int.class, DialogInterface.OnCancelListener.class);
        if (constructor == null) return;
        XposedBridge.hookMethod(constructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeConstructor3(targetClazz.cast(param.thisObject), (Context) param.args[0], (int) param.args[1], (DialogInterface.OnCancelListener) param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterConstructor3(targetClazz.cast(param.thisObject), (Context) param.args[0], (int) param.args[1], (DialogInterface.OnCancelListener) param.args[2]);
            }
        });
    }

    protected void onBeforeConstructor3(@NonNull T hookDialog, Context context, int themeResId, @NonNull DialogInterface.OnCancelListener cancelListener) {

    }

    protected void onAfterConstructor3(@NonNull T hookDialog, Context context, int themeResId, @NonNull DialogInterface.OnCancelListener cancelListener) {

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

    protected void onBeforeCreate(@NonNull T hookDialog, Bundle bundle) {

    }

    protected void onAfterCreate(@NonNull T hookDialog, Bundle bundle) {

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

    protected void onBeforeStart(@NonNull T hookDialog) {

    }

    protected void onAfterStart(@NonNull T hookDialog) {

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

    protected void onBeforeStop(@NonNull T hookDialog) {

    }

    protected void onAfterStop(@NonNull T hookDialog) {

    }

    // Hook create
    private void hookCreate(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "create");
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeCreate(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterCreate(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void beforeCreate(@NonNull T hookDialog) {

    }

    protected void afterCreate(@NonNull T hookDialog) {

    }

    // Hook show
    private void hookShow(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "show");
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeShow(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterShow(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void beforeShow(@NonNull T hookDialog) {

    }

    protected void afterShow(@NonNull T hookDialog) {

    }

    // Hook hide
    private void hookHide(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "hide");
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeHide(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterHide(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void beforeHide(@NonNull T hookDialog) {

    }

    protected void afterHide(@NonNull T hookDialog) {

    }

    // Hook dismiss
    private void hookDismiss(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "dismiss");
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeDismiss(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterDismiss(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void beforeDismiss(@NonNull T hookDialog) {

    }

    protected void afterDismiss(@NonNull T hookDialog) {

    }

    // Hook setContentView 1
    private void hookSetContentView1(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "setContentView", int.class);
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeSetContentView1(targetClazz.cast(param.thisObject), (int) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterSetContentView1(targetClazz.cast(param.thisObject), (int) param.args[0]);
            }
        });
    }

    protected void beforeSetContentView1(@NonNull T hookDialog, @LayoutRes int layoutResID) {

    }

    protected void afterSetContentView1(@NonNull T hookDialog, @LayoutRes int layoutResID) {

    }

    // Hook setContentView 2
    private void hookSetContentView2(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "setContentView", View.class);
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeSetContentView2(targetClazz.cast(param.thisObject), (View) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterSetContentView2(targetClazz.cast(param.thisObject), (View) param.args[0]);
            }
        });
    }

    protected void beforeSetContentView2(@NonNull T hookDialog, @NonNull View view) {

    }

    protected void afterSetContentView2(@NonNull T hookDialog, @NonNull View view) {

    }

    // Hook setContentView 3
    private void hookSetContentView3(Class<T> targetClazz) {
        Method onStop = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "setContentView", View.class, ViewGroup.LayoutParams.class);
        if (onStop == null) return;
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                beforeSetContentView3(targetClazz.cast(param.thisObject), (View) param.args[0], (ViewGroup.LayoutParams) param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                afterSetContentView3(targetClazz.cast(param.thisObject), (View) param.args[0], (ViewGroup.LayoutParams) param.args[1]);
            }
        });
    }

    protected void beforeSetContentView3(@NonNull T hookDialog, @NonNull View view, @NonNull ViewGroup.LayoutParams params) {

    }

    protected void afterSetContentView3(@NonNull T hookDialog, @NonNull View view, @NonNull ViewGroup.LayoutParams params) {

    }


    // Hook More
    protected void hookMore(@NonNull XC_LoadPackage.LoadPackageParam lpparam, @NonNull Class<T> targetClazz) {

    }
}
