package com.mm.freedom.hook.douyin

import android.os.Bundle
import com.mm.freedom.config.ErrorLog
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.config.Version
import com.mm.freedom.hook.base.BaseActivityHelper
import com.mm.freedom.utils.GLockUtils
import com.ss.android.ugc.aweme.main.MainActivity
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Hook MainActivity
 */
class HMainActivity(lpparam: XC_LoadPackage.LoadPackageParam) :
    BaseActivityHelper<MainActivity>(lpparam, MainActivity::class.java) {

    override fun onAfterCreate(hookActivity: MainActivity, bundle: Bundle?) {

        //删除临时文件锁, 用不到了
        GLockUtils.fileUnlock(hookActivity, ".init")
        GLockUtils.fileUnlock(hookActivity, ".gifEmoji")
        GLockUtils.fileUnlock(hookActivity, ".running")

        //加载模块设置
        val moduleDirectory = ModuleConfig.getModuleDirectory(application)
        if (moduleDirectory.absolutePath.contains("com.ss.android.ugc.aweme")) {
            handler.post { showToast(hookActivity, "抖音未获得文件读写权限") }
            return
        }

        //删除临时文件
        ModuleConfig.getModulePrivateDirectory(application).delete()

        //初始化错误日志
        ErrorLog.init(ModuleConfig.getModuleConfigDir(application).absolutePath)

        //提示附加成功
        handler.post { showToast(hookActivity, "Freedom Attach!") }

        //检测是否有更新
        ModuleConfig.getModuleConfig(application) {
            val versionName = it.versionName
            Version.getRemoteReleasesLatest { versionConfig ->
                if (Version.compare(versionName, versionConfig.name) == 1) {
                    handler.post { showToast(hookActivity, "Freedom有新版本了!") }
                }
            }
        }
    }
}