package com.mm.freedom.xposed.extension

import android.content.Context
import android.content.res.XModuleResources
import android.util.Log
import android.widget.Toast
import com.mm.freedom.view.CustomToast
import com.mm.freedom.xposed.extension.bridge.ConstructorHook
import com.mm.freedom.xposed.extension.bridge.MethodHook
import com.mm.freedom.xposed.extension.bridge.MethodHookImpl
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method
import java.time.Duration
import java.util.Timer
import java.util.TimerTask

//构造方法
fun Class<*>.hookConstructor(
    vararg argsTypes: Any,
    block: ConstructorHook.() -> Unit,
): KtXposedHelpers {
    return KtXposedHelpers
        .hookClass(this)
        .constructor(*argsTypes) { block.invoke(this) }
}

fun String.hookConstructor(
    vararg argsTypes: Any,
    classLoader: ClassLoader,
    block: ConstructorHook.() -> Unit,
): KtXposedHelpers {
    return XposedHelpers.findClass(this, classLoader)
        .hookConstructor(*argsTypes) { block.invoke(this) }
}


//普通方法
fun Class<*>.hookMethod(
    method: String,
    vararg argsTypes: Any,
    block: MethodHook.() -> Unit,
): KtXposedHelpers {
    return KtXposedHelpers
        .hookClass(this)
        .method(method, *argsTypes) { block.invoke(this) }
}

fun Method.hook(block: MethodHook.() -> Unit): Unit {
    val methodHookImpl = MethodHookImpl(this)
    block.invoke(methodHookImpl)
    methodHookImpl.start()
}

fun String.hookMethod(
    method: String,
    classLoader: ClassLoader,
    vararg argsTypes: Any,
    block: MethodHook.() -> Unit,
): KtXposedHelpers {
    return XposedHelpers.findClass(this, classLoader)
        .hookMethod(method, *argsTypes) { block.invoke(this) }
}


//ClassLoader
fun ClassLoader.hookClass(clazz: Class<*>): KtXposedHelpers {
    return KtXposedHelpers.hookClass(clazz.name, this)
}


//Xposed
fun XC_LoadPackage.LoadPackageParam.hookClass(clazz: Class<*>): KtXposedHelpers {
    return KtXposedHelpers.hookClass(clazz.name, this.classLoader)
}


//其他扩展
fun CharSequence.xLog(): CharSequence {
    XposedBridge.log("xpler: $this")
    return this
}

fun Throwable.xLog(): Throwable {
    XposedBridge.log("xpler: ${Log.getStackTraceString(this)}")
    return this
}

fun CharSequence.showToast(context: Context): CharSequence {
    val toast = Toast.makeText(context, null, Toast.LENGTH_LONG)
    toast.setText(this)
    toast.show()
    return this
}

fun Context.showToast(msg: String): Context {
    msg.showToast(this)
    return this
}

private var mModulePath: String? = null
private var mModuleRes: XModuleResources? = null
fun KtXposedHelpers.Companion.initModule(modulePath: String, moduleRes: XModuleResources) {
    mModulePath = modulePath
    mModuleRes = moduleRes
}

fun KtXposedHelpers.Companion.getModulePath() = mModulePath!!
fun KtXposedHelpers.Companion.getModuleRes() = mModuleRes!!
