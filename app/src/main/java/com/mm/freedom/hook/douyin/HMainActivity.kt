package com.mm.freedom.hook.douyin

import android.os.Bundle
import android.widget.Toast
import com.mm.freedom.PackNames
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.hook.base.BaseActivityHelper
import com.mm.freedom.utils.GLockUtils
import com.mm.freedom.utils.GLogUtils
import com.ss.android.ugc.aweme.main.MainActivity
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Hook MainActivity
 */
class HMainActivity(lpparam: XC_LoadPackage.LoadPackageParam?) :
    BaseActivityHelper<MainActivity>(lpparam, MainActivity::class.java) {

    override fun onAfterCreate(hookActivity: MainActivity, bundle: Bundle?) {
        lockRunning(".init") {
            val moduleDirectory = ModuleConfig.getModuleDirectory(application)
            if (moduleDirectory.absolutePath.contains("com.ss.android.ugc.aweme")) {
                handler.post { showToast(hookActivity, "抖音未获得文件读写权限") }
                return@lockRunning
            }
            ModuleConfig.getModulePrivateDirectory(application).delete()
            handler.post { showToast(hookActivity, "Freedom Attach!") }
        }
    }
}