package com.mm.freedom.hook.douyin

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.utils.GBitmapUtils
import com.mm.freedom.utils.GLogUtils
import com.mm.freedom.utils.GPathUtils
import com.mm.freedom.utils.GViewUtils
import com.mm.freedom.xposed.DialogHelper
import com.ss.android.ugc.aweme.emoji.model.Emoji
import com.ss.android.ugc.aweme.emoji.store.view.EmojiBottomSheetDialog
import com.ss.android.ugc.aweme.emoji.views.EmojiDetailDialog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

/**
 * 抖音表情详情查看弹层(聊天页, 弹出的底部弹层) EmojiDetailDialog
 *
 */
@Deprecated("已经废弃")
class HEmojiBottomSheetDialog(lpparam: XC_LoadPackage.LoadPackageParam?) :
    DialogHelper<EmojiBottomSheetDialog>(lpparam, EmojiBottomSheetDialog::class.java) {

    override fun onAfterCreate(hookDialog: EmojiBottomSheetDialog, bundle: Bundle?) {
        ModuleConfig.getModuleConfig(application) {
            if (!it.isSaveEmojiValue) return@getModuleConfig
            if (hookDialog is EmojiDetailDialog) {
                setEmojiDetail(hookDialog, it)
            }
        }
    }

    // 聊天页 -> 表情详情查看弹层 EmojiDetailDialog
    private fun setEmojiDetail(hookDialog: EmojiBottomSheetDialog, config: Config) {
        //handler.post { GLogUtils.xLogAndToast(hookDialog.context, "进入表情页面") }
        //获取到屏幕上的布局, (统一ID: android.R.id.content, 也就是 setContentView 设置的布局)
        val contentView = hookDialog.window!!.decorView.findViewById<FrameLayout>(android.R.id.content)
        //获取到该布局下的 所有ImageView
        val findViews = GViewUtils.findViews(contentView, ImageView::class.java)
        //最后一个是表情图片, 类名(RemoteImageView), 后期可能发生变动
        val remoteImageView = findViews.last()
        if (remoteImageView::class.java.name.contains("RemoteImageView")) {
            remoteImageView.isLongClickable = true
            remoteImageView.setOnLongClickListener {
                // 获取表情内容
                val drawable = remoteImageView.drawable
                val bitmap = drawable.toBitmap(width = drawable.bounds.width(), height = drawable.bounds.height())
                savePicture(hookDialog, bitmap, config)
                true
            }
        }
    }

    // 保存图片到本地
    private fun savePicture(hookDialog: EmojiBottomSheetDialog, bitmap: Bitmap, config: Config) {
        // 保存路径
        var path = File(GPathUtils.getStoragePath(hookDialog.context), Environment.DIRECTORY_DCIM)
        // 如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookDialog.context, "Emoji")
        }

        // 新起一个线程, 保存图片表情
        Thread {
            //以时间戳作为文件名
            val result = GBitmapUtils.bitmap2Path(bitmap, path.absolutePath, "${System.currentTimeMillis()}.png")
            handler.post {
                GLogUtils.xLogAndToast(hookDialog.context, "保存${if (result) "成功^_^" else "失败~_~"}!")
            }
        }.start()
    }
}