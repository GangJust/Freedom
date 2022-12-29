package com.mm.freedom.xposed.extension.bridge

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Member

/// 对普通方法的 Hook 封装
interface MethodHook : KtHook {
    fun onBefore(block: (param: XC_MethodHook.MethodHookParam) -> Unit)
    fun onAfter(block: (param: XC_MethodHook.MethodHookParam) -> Unit)
    fun onReplace(block: (param: XC_MethodHook.MethodHookParam) -> Any)
}

/// 实现类
open class MethodHookImpl(private var method: Member) : MethodHook {
    private var beforeBlock: ((param: XC_MethodHook.MethodHookParam) -> Unit)? = null
    private var afterBlock: ((param: XC_MethodHook.MethodHookParam) -> Unit)? = null
    private var replaceBlock: ((param: XC_MethodHook.MethodHookParam) -> Any)? = null

    constructor(clazz: Class<*>, methodName: String, vararg argsTypes: Any) :
            this(XposedHelpers.findMethodExact(clazz, methodName, *argsTypes))

    override fun onBefore(block: (param: XC_MethodHook.MethodHookParam) -> Unit) {
        this.beforeBlock = block
    }

    override fun onAfter(block: (param: XC_MethodHook.MethodHookParam) -> Unit) {
        this.afterBlock = block
    }

    override fun onReplace(block: (param: XC_MethodHook.MethodHookParam) -> Any) {
        this.replaceBlock = block
    }

    /// 开启 hook
    fun start() {
        if (replaceBlock != null) {
            XposedBridge.hookMethod(method, object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return this@MethodHookImpl.replaceBlock!!.invoke(param)
                }
            })
        } else {
            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    this@MethodHookImpl.beforeBlock?.invoke(param)
                }


                override fun afterHookedMethod(param: MethodHookParam) {
                    this@MethodHookImpl.afterBlock?.invoke(param)
                }
            })
        }
    }
}