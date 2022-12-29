package com.mm.freedom.hook.douyin

import com.mm.freedom.xposed.XposedExtendHelper
import de.robv.android.xposed.callbacks.XC_LoadPackage

// 抖音Hook
class HookDY(lpparam: XC_LoadPackage.LoadPackageParam) : XposedExtendHelper(lpparam) {
    init {
        HMainActivity(lpparam)
        HAbsActivity(lpparam)
    }
}
