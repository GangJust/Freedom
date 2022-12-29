package com.mm.freedom;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.mm.freedom.config.ModuleConfig;
import com.mm.freedom.hook.douyin.HookDY;
import com.mm.freedom.utils.GLockUtils;
import com.mm.freedom.utils.XClassLoader;
import com.mm.freedom.xposed.extension.KtXposedHelpers;
import com.mm.freedom.xposed.extension.KtXposedMoreKt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AttachHook implements IXposedHookLoadPackage {
    protected XClassLoader xClassLoader;
    private static final MainHook mainHook = new MainHook();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //务必过滤掉非必要应用
        if (Arrays.asList(PackNames.packNames).contains(lpparam.packageName)) {
            if (isInjecter(lpparam.processName)) return;
            attach(lpparam);
        }
    }

    private void attach(XC_LoadPackage.LoadPackageParam lpparam) {
        //GLogUtil.xLog("attach: " + lpparam.packageName);
        XposedHelpers.findAndHookMethod(
                Application.class,
                "attach",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) param.args[0];
                        ClassLoader mineLoader = AttachHook.class.getClassLoader();
                        ClassLoader hookLoader = context.getClassLoader();

                        // 获取当前ClassLoader
                        Field fParent = ClassLoader.class.getDeclaredField("parent");
                        fParent.setAccessible(true);
                        ClassLoader currLoader = (ClassLoader) fParent.get(mineLoader);
                        if (currLoader == null) currLoader = XposedBridge.class.getClassLoader();

                        // 替换当前ClassLoader
                        if (!currLoader.getClass().getName().equals(XClassLoader.class.getName())) {
                            fParent.set(mineLoader, xClassLoader = new XClassLoader(currLoader, hookLoader));
                        }

                        // 首先, 进程判断, 只有主进程, 才执行后续逻辑
                        String processName = getProcessName(context);
                        if (processName != null && !processName.equals(context.getPackageName())) return;

                        startModule(lpparam, (Application) param.thisObject);
                    }
                }
        );
    }

    /**
     * 开启模块运行
     *
     * @param lpparam
     * @throws Throwable
     */
    private void startModule(XC_LoadPackage.LoadPackageParam lpparam, Application application) throws Throwable {
        //调用 MainHook
        if (xClassLoader != null) lpparam.classLoader = xClassLoader;
        mainHook.handleLoadPackage(lpparam);
        mainHook.handleLoadPackage(lpparam, application);
    }

    /**
     * 获取进程名
     *
     * @param context 应用上下文
     * @return 进程名
     */
    private String getProcessName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) return null;
            for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
                if (proInfo.pid == android.os.Process.myPid()) {
                    if (proInfo.processName != null) {
                        return proInfo.processName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 防止重复执行Hook代码, 限制handleLoadPackage被单个进程多次执行的问题
     *
     * @param flag 判断标识,针对不同Hook代码分别进行判断
     * @return 是否已经注入Hook代码
     */
    private boolean isInjecter(String flag) {
        try {
            if (TextUtils.isEmpty(flag)) return false;
            Field methodCacheField = XposedHelpers.class.getDeclaredField("methodCache");
            methodCacheField.setAccessible(true);
            Map<String, Method> methodCache = (Map<String, Method>) methodCacheField.get(null);
            Method method = XposedHelpers.findMethodBestMatch(Application.class, "onCreate");
            String key = String.format("%s#%s", flag, method.getName());
            if (methodCache.containsKey(key)) return true;
            methodCache.put(key, method);
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
