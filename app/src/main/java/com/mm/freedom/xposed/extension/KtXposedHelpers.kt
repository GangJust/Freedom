package com.mm.freedom.xposed.extension

import com.mm.freedom.xposed.extension.bridge.ConstructorHook
import com.mm.freedom.xposed.extension.bridge.ConstructorHookImpl
import com.mm.freedom.xposed.extension.bridge.MethodHook
import com.mm.freedom.xposed.extension.bridge.MethodHookImpl
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method

/// Xposed hook 的基础封装
class KtXposedHelpers {
    private var clazz: Class<*>? = null

    companion object {
        private val instances = KtXposedHelpers()

        fun hookClass(clazz: Class<*>): KtXposedHelpers {
            instances.clazz = clazz
            return instances
        }

        fun hookClass(className: String, classLoader: ClassLoader): KtXposedHelpers {
            val findClass = XposedHelpers.findClass(className, classLoader)
            instances.clazz = findClass
            return instances
        }
    }

    fun constructor(vararg argsTypes: Any, block: ConstructorHook.() -> Unit): KtXposedHelpers {
        val constructorHookImpl = ConstructorHookImpl(clazz!!, *argsTypes)
        block.invoke(constructorHookImpl)
        constructorHookImpl.start()
        return this
    }

    fun method(method: Method, block: MethodHook.() -> Unit): KtXposedHelpers {
        val methodHookImpl = MethodHookImpl(method)
        block.invoke(methodHookImpl)
        methodHookImpl.start()
        return this
    }

    fun method(method: String, vararg argsTypes: Any, block: MethodHook.() -> Unit): KtXposedHelpers {
        val methodHookImpl = MethodHookImpl(clazz!!, method, *argsTypes)
        block.invoke(methodHookImpl)
        methodHookImpl.start()
        return this
    }
}