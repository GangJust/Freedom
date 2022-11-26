package com.mm.freedom.xposed;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed Fragment Hook Extend Helper
 * 尽量不要持有 hook 对象, 该类没有经过测试
 *
 * @param <T> 继承至 android.app.Fragment 的子类 (适配旧应用)
 * @author Gang, GitHub Home: https://github.com/GangJust
 */
public abstract class FragmentHelper<T extends Fragment> extends XposedExtendHelper {

    protected FragmentHelper(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {
        super(lpparam);
        hookOnInflate1(targetClazz);
        hookOnInflate2(targetClazz);
        hookOnInflate3(targetClazz);
        hookOnAttach(targetClazz);
        hookOnCreate(targetClazz);
        hookOnCreateView(targetClazz);
        hookOnViewCreated(targetClazz);
        hookOnActivityCreated(targetClazz);
        hookOnStart(targetClazz);
        hookOnResume(targetClazz);
        hookOnPause(targetClazz);
        hookOnStop(targetClazz);
        hookOnDestroyView(targetClazz);
        hookOnDestroy(targetClazz);
        hookOnDetach(targetClazz);
        hookMore(lpparam, targetClazz);
    }

    // Hook onInflate 1
    private void hookOnInflate1(Class<T> targetClazz) {
        Method onInflate = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onInflate", AttributeSet.class, Bundle.class);
        if (onInflate == null) return;
        XposedBridge.hookMethod(onInflate, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeInflate1(targetClazz.cast(param.thisObject), (AttributeSet) param.args[0], (Bundle) param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterInflate1(targetClazz.cast(param.thisObject), (AttributeSet) param.args[0], (Bundle) param.args[1]);
            }
        });
    }

    protected void onBeforeInflate1(@NonNull T hookFragment, AttributeSet attrs, Bundle savedInstanceState) {

    }

    protected void onAfterInflate1(@NonNull T hookFragment, AttributeSet attrs, Bundle savedInstanceState) {

    }

    // Hook onInflate 2
    private void hookOnInflate2(Class<T> targetClazz) {
        Method onInflate = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onInflate", Context.class, AttributeSet.class, Bundle.class);
        if (onInflate == null) return;
        XposedBridge.hookMethod(onInflate, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeInflate2(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (Bundle) param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterInflate2(targetClazz.cast(param.thisObject), (Context) param.args[0], (AttributeSet) param.args[1], (Bundle) param.args[2]);
            }
        });
    }

    protected void onBeforeInflate2(@NonNull T hookFragment, Context context, AttributeSet attrs, Bundle savedInstanceState) {

    }

    protected void onAfterInflate2(@NonNull T hookFragment, Context context, AttributeSet attrs, Bundle savedInstanceState) {

    }

    // Hook onInflate 1
    private void hookOnInflate3(Class<T> targetClazz) {
        Method onInflate = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onInflate", Activity.class, Bundle.class);
        if (onInflate == null) return;
        XposedBridge.hookMethod(onInflate, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeInflate3(targetClazz.cast(param.thisObject), (Activity) param.args[0], (AttributeSet) param.args[1], (Bundle) param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterInflate3(targetClazz.cast(param.thisObject), (Activity) param.args[0], (AttributeSet) param.args[1], (Bundle) param.args[2]);
            }
        });
    }

    protected void onBeforeInflate3(@NonNull T hookFragment, Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

    }

    protected void onAfterInflate3(@NonNull T hookFragment, Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

    }

    // Hook onAttach
    private void hookOnAttach(Class<T> targetClazz) {
        Method onAttach = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onAttach", Context.class);
        if (onAttach == null) return;
        XposedBridge.hookMethod(onAttach, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeAttach(targetClazz.cast(param.thisObject), (Context) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterAttach(targetClazz.cast(param.thisObject), (Context) param.args[0]);
            }
        });
    }

    protected void onBeforeAttach(@NonNull T hookFragment, Context context) {

    }

    protected void onAfterAttach(@NonNull T hookFragment, Context context) {

    }

    // Hook onAttach
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

    protected void onBeforeCreate(@NonNull T hookFragment, Bundle savedInstanceState) {

    }

    protected void onAfterCreate(@NonNull T hookFragment, Bundle savedInstanceState) {

    }

    // Hook onCreateView
    private void hookOnCreateView(Class<T> targetClazz) {
        Method onCreateView = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class);
        if (onCreateView == null) return;
        XposedBridge.hookMethod(onCreateView, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                View view = onBeforeCreateView(targetClazz.cast(param.thisObject), (LayoutInflater) param.args[0], (ViewGroup) param.args[1], (Bundle) param.args[2]);
                if (view != null) param.setResult(view);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View view = onAfterCreateView(targetClazz.cast(param.thisObject), (LayoutInflater) param.args[0], (ViewGroup) param.args[1], (Bundle) param.args[2]);
                if (view != null) param.setResult(view);
            }
        });
    }

    protected View onBeforeCreateView(@NonNull T hookFragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    protected View onAfterCreateView(@NonNull T hookFragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    // Hook onViewCreated
    private void hookOnViewCreated(Class<T> targetClazz) {
        Method onViewCreated = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onViewCreated", View.class, Bundle.class);
        if (onViewCreated == null) return;
        XposedBridge.hookMethod(onViewCreated, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeViewCreated(targetClazz.cast(param.thisObject), (View) param.args[0], (Bundle) param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterViewCreated(targetClazz.cast(param.thisObject), (View) param.args[0], (Bundle) param.args[1]);
            }
        });
    }

    protected void onBeforeViewCreated(@NonNull T hookFragment, View view, Bundle savedInstanceState) {

    }

    protected void onAfterViewCreated(@NonNull T hookFragment, View view, Bundle savedInstanceState) {

    }

    // Hook onActivityCreated
    private void hookOnActivityCreated(Class<T> targetClazz) {
        Method onActivityCreated = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onActivityCreated", Bundle.class);
        if (onActivityCreated == null) return;
        XposedBridge.hookMethod(onActivityCreated, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeActivityCreated(targetClazz.cast(param.thisObject), (Bundle) param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterActivityCreated(targetClazz.cast(param.thisObject), (Bundle) param.args[0]);
            }
        });
    }

    protected void onBeforeActivityCreated(@NonNull T hookFragment, Bundle savedInstanceState) {

    }

    protected void onAfterActivityCreated(@NonNull T hookFragment, Bundle savedInstanceState) {

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

    protected void onBeforeStart(@NonNull T hookFragment) {

    }

    protected void onAfterStart(@NonNull T hookFragment) {

    }

    // Hook onResume
    private void hookOnResume(Class<T> targetClazz) {
        Method onStart = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onResume");
        if (onStart == null) return;
        XposedBridge.hookMethod(onStart, new XC_MethodHook() {
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

    protected void onAfterResume(@NonNull T hookFragment) {

    }

    protected void onBeforeResume(@NonNull T hookFragment) {

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

    protected void onBeforePause(@NonNull T hookFragment) {

    }

    protected void onAfterPause(@NonNull T hookFragment) {

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

    protected void onBeforeStop(@NonNull T hookFragment) {

    }

    protected void onAfterStop(@NonNull T hookFragment) {

    }

    // Hook onDestroyView
    private void hookOnDestroyView(Class<T> targetClazz) {
        Method onDestroyView = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onDestroyView");
        if (onDestroyView == null) return;
        XposedBridge.hookMethod(onDestroyView, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeDestroyView(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterDestroyView(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeDestroyView(@NonNull T hookFragment) {

    }

    protected void onAfterDestroyView(@NonNull T hookFragment) {

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

    protected void onBeforeDestroy(@NonNull T hookFragment) {

    }

    protected void onAfterDestroy(@NonNull T hookFragment) {

    }

    // Hook onDetach
    private void hookOnDetach(Class<T> targetClazz) {
        Method onDetach = XposedHelpers.findMethodExactIfExists(targetClazz.getName(), lpparam.classLoader, "onDetach");
        if (onDetach == null) return;
        XposedBridge.hookMethod(onDetach, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                onBeforeDetach(targetClazz.cast(param.thisObject));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                onAfterDetach(targetClazz.cast(param.thisObject));
            }
        });
    }

    protected void onBeforeDetach(@NonNull T hookFragment) {

    }

    protected void onAfterDetach(@NonNull T hookFragment) {

    }

    // Hook More
    protected void hookMore(XC_LoadPackage.LoadPackageParam lpparam, Class<T> targetClazz) {

    }
}
