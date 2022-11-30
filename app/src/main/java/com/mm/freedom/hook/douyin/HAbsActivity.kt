package com.mm.freedom.hook.douyin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bytedance.ies.uikit.base.AbsActivity
import com.freegang.androidtemplate.base.interfaces.TemplateCallDefault
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.hook.base.BaseActivityHelper
import com.mm.freedom.utils.*
import com.ss.android.ugc.aweme.comment.model.CommentImageStruct
import com.ss.android.ugc.aweme.comment.ui.GifEmojiDetailActivity
import com.ss.android.ugc.aweme.comment.ui.ImageDetailActivity
import com.ss.android.ugc.aweme.detail.ui.DetailActivity
import com.ss.android.ugc.aweme.emoji.model.Emoji
import com.ss.android.ugc.aweme.main.MainActivity
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * 抖音基础类, 基本上所有的Activity类都是继承至它的, 至少目前用到的所有类
 */
class HAbsActivity(lpparam: XC_LoadPackage.LoadPackageParam?) :
    BaseActivityHelper<AbsActivity>(lpparam, AbsActivity::class.java), TemplateCallDefault {
    private lateinit var config: Config
    private var onPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onAfterCreate(hookActivity: AbsActivity, bundle: Bundle?) {
        lockRunning(".gifEmoji") {
            ModuleConfig.getModuleConfig(application) {
                config = it
                if (isInstance(hookActivity, GifEmojiDetailActivity::class.java)
                    || isInstance(hookActivity, ImageDetailActivity::class.java)
                ) {
                    //GLogUtils.xLog("Emoji!!")
                    hookEmoji(hookActivity)
                }
            }
        }
    }

    override fun onBeforeResume(hookActivity: AbsActivity) {
        lockRunning(".running") {
            ModuleConfig.getModuleConfig(application) {
                config = it
                if (isInstance(hookActivity, MainActivity::class.java)
                    || isInstance(hookActivity, DetailActivity::class.java)
                ) {
                    //GLogUtils.xLog("Video!!")
                    hookVideo(hookActivity)
                }
            }
        }
    }

    override fun onAfterPause(hookActivity: AbsActivity) {
        removeClipChangedListener(hookActivity)
    }

    /// 视频/图片/背景音乐 (分享复制监听)
    private fun hookVideo(hookActivity: AbsActivity) {
        addClipChangedListener(hookActivity)
    }

    // 添加剪贴板复制监听
    private fun addClipChangedListener(hookActivity: AbsActivity) {
        val clipboardManager = hookActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        onPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
            val clipData = clipboardManager.primaryClip
            if (clipboardManager.hasPrimaryClip() && clipData!!.itemCount > 0) {
                //获取剪贴板内容
                val clipDataItem = clipData.getItemAt(0)
                val shareText = clipDataItem.text.toString()
                //截取URL, 视频链接
                if (shareText.contains("http")) {
                    //跳过直播链接, 按文本检查
                    if (shareText.contains("【抖音】") && shareText.contains("正在直播") && shareText.contains("一起支持")) {
                        showToast(hookActivity, "不支持直播视频")
                        return@OnPrimaryClipChangedListener
                    }
                    if (config.isClipDataDetailValue) {
                        //handler.post { GLogUtils.xLogAndToast(hookActivity, "复制成功!\n$shareText"); }
                        handler.post { showToast(hookActivity, "复制成功!\n$shareText") }
                    } else {
                        handler.post { showToast(hookActivity, "复制成功!") }
                    }
                    val start = shareText.indexOf("http")
                    // 一般这个截取逻辑能用到死, 但是不排除抖音更新分享文本格式, 如果真更新再说.
                    val sortUrl = shareText.substring(start)
                    //获取真实地址
                    getOriginalUrl(sortUrl) { originalUrl: String ->
                        if (originalUrl.isEmpty()) {
                            handler.post { showToast(hookActivity, "没有获取到该视频的真实地址") }
                            return@getOriginalUrl
                        }
                        //获取视频id
                        val videoId = getVideoId(originalUrl)
                        if (videoId.isEmpty()) {
                            handler.post { showToast(hookActivity, "没有获取到该视频的ID") }
                            return@getOriginalUrl
                        }
                        //解析视频
                        getItemInfo(videoId) { itemInfoJson: String ->
                            parseItemInfo(hookActivity, itemInfoJson)
                        }
                    }
                }
            }
        }
        clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    // 移除剪贴板复制监听
    private fun removeClipChangedListener(hookActivity: AbsActivity) {
        val clipboardManager = hookActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
        onPrimaryClipChangedListener = null
    }

    //获取真实地址
    private fun getOriginalUrl(sortUrl: String, callback: (String) -> Unit) {
        //新起一个线程, 获取实际地址(短链接重定向后, 指向真实地址)
        Thread {
            val originalUrl = GHttpUtils.getRedirectsUrl(sortUrl)
            callback(originalUrl)
        }.start()
    }

    //获取视频ID
    private fun getVideoId(originalUrl: String): String {
        //正则表达式, 获取视频ID
        val compile = Pattern.compile("([0-9]+)(/?)") //最后一个斜线可能没有
        val matcher = compile.matcher(originalUrl)
        return if (matcher.find() && matcher.groupCount() >= 1) matcher.group(1) ?: "" else ""
    }

    //通过视频ID解析出帖子(抖音中一条视频就是一个帖子)信息, 返回一个JSON数据
    private fun getItemInfo(videoId: String, callback: (String) -> Unit) {
        //获取基本信息, 固定url, 后面有更新再说
        Thread {
            val itemInfoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$videoId"
            val itemInfoJson = GHttpUtils.get(itemInfoUrl)
            callback(itemInfoJson)
        }.start()
    }

    //解析 视频、音频、图片
    private fun parseItemInfo(hookActivity: AbsActivity, itemJson: String) {
        //解析出 标题、视频、背景音乐
        if (itemJson.isNotEmpty()) {
            val parse = GJSONUtils.parse(itemJson)
            val itemList = GJSONUtils.getArray(parse, "item_list")
            if (itemList.length() >= 1) {
                //默认下载后保存的名称, 如果没取到分享的视频名的话
                var shareTitle = "download"
                //操作标题
                val optionNames = mutableListOf<String>()
                //操作URL, 和[optionNames]长度相同
                val optionUrls = mutableListOf<String>()
                //获取第一项
                val item = GJSONUtils.get(itemList, 0)
                //获取分享标题
                if (GJSONUtils.hasKey(item, "share_info")) {
                    //分享标题: item_list -> share_info -> share_title
                    shareTitle = GJSONUtils.getString(GJSONUtils.get(item, "share_info"), "share_title")
                }
                //获取图片(如果有图片, 则图片下载项, 否则显示视频下载项)
                if (!GJSONUtils.isNull(item, "images")) {
                    optionNames.add("图片")
                    //图片url: item_list -> images -> url_list
                    var urls = ""
                    val images = GJSONUtils.getArray(item, "images")
                    for (i in 0 until images.length()) {
                        val imageItem = GJSONUtils.get(images, i)
                        val urlList = GJSONUtils.getArray(imageItem, "url_list")
                        urls = "${urls}|分割|${GJSONUtils.getString(urlList, urlList.length() - 1)}"
                    }
                    optionUrls.add(urls)
                } else if (GJSONUtils.hasKey(item, "video")) {
                    //视频url: item_list -> video -> play_addr -> url_list -> [0] 项
                    val urlList = GJSONUtils.getArrayUntil(item, "video", "play_addr", "url_list")
                    val videoUrl = GJSONUtils.getString(urlList, urlList.length() - 1).replace("playwm", "play") //替换无水印关键字
                    optionNames.add("视频")
                    optionUrls.add(videoUrl)
                }
                //获取音频
                if (GJSONUtils.hasKey(item, "music")) {
                    //音频url: item_list -> music -> play_url -> uri 项
                    val urlList = GJSONUtils.getArrayUntil(item, "music", "play_url", "url_list")
                    val musicUrl = GJSONUtils.getString(urlList, urlList.length() - 1)
                    optionNames.add("背景音乐")
                    optionUrls.add(musicUrl)
                }
                //构建弹层, 并显示
                showOptionDialog(hookActivity, shareTitle, optionNames.toTypedArray(), optionUrls.toTypedArray())
            } else {
                handler.post { showToast(hookActivity, "没有获取到这条视频的基本信息") }
            }
        } else {
            handler.post { showToast(hookActivity, "没有获取到这条视频的基本信息") }
        }
    }

    //显示操作弹窗
    private fun showOptionDialog(
        hookActivity: AbsActivity,
        shareTitle: String,
        optionNames: Array<String>,
        optionUrls: Array<String>
    ) {
        val builder = AlertDialog.Builder(hookActivity)
        builder.setTitle("Freedom")
        builder.setItems(optionNames) { dialog, which ->
            when (which) {
                0 -> {
                    if (optionNames.filter { it.contains("图片") }.size == 1) {
                        val urls = optionUrls[which].split("|分割|")
                        downloadPicture(hookActivity, urls, shareTitle)
                    } else {
                        downloadVideo(hookActivity, optionUrls[which], "$shareTitle.mp4")
                    }
                }
                1 -> downloadMusic(hookActivity, optionUrls[which], "$shareTitle.mp3")
            }
            dialog.dismiss()
        }
        handler.post { builder.show() }
    }

    // 下载视频
    private fun downloadVideo(hookActivity: AbsActivity, url: String, filename: String) {
        if (url.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到这条视频的URL地址") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Video/";
        var path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookActivity, "Video")
        }
        downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
    }

    // 下载音频
    private fun downloadMusic(hookActivity: AbsActivity, url: String, filename: String) {
        if (url.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到这条视频的背景音乐") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Music/";
        var path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_MUSIC)
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookActivity, "Music")
        }
        downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
    }

    // 下载图片(下载图片时filename作为文件夹)
    private fun downloadPicture(hookActivity: AbsActivity, urls: List<String>, filename: String) {
        if (urls.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到图片") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Picture/";
        var path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookActivity, "Picture").addChildDir(filename)
        }
        downloadFiles(hookActivity, urls, path.absolutePath, "$filename.jpeg")
    }

    // 下载文件
    private fun downloadFileAndShowDialog(hookActivity: AbsActivity, url: String, path: String, filename: String) {
        //GLogUtils.xLog("下载: " + url);
        //GLogUtils.xLog("路径: " + path);
        //GLogUtils.xLog("文件名: " + filename);
        val progressDialog = ProgressDialog(hookActivity)
        progressDialog.setTitle("开始下载")
        progressDialog.setCancelable(false) //禁止返回取消
        progressDialog.setCanceledOnTouchOutside(false) //禁止触摸外部取消
        progressDialog.progress = 0
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.max = 100
        progressDialog.show()

        Thread {
            val download = GHttpUtils.download(url, path, filename) { real: Long, total: Long ->
                handler.post {
                    progressDialog.progress = (real * 100 / total).toInt()
                }
            }

            //得到下载结果再取消进度框
            handler.post {
                progressDialog.dismiss()
                //GLogUtils.xLogAndToast(hookActivity, download ? "下载成功!" : "下载失败, 请检查抖音的文件读写权限!");
                showToast(hookActivity, if (download) "下载成功!" else "下载失败, 请检查抖音的文件读写权限!")
            }
        }.start()
    }

    // 下载多个文件
    private fun downloadFiles(hookActivity: AbsActivity, urls: List<String>, path: String, filename: String) {
        val urls = urls.filter { it.isNotEmpty() && it.isNotEmpty() }
        if (urls.isEmpty()) {
            handler.post { showToast(hookActivity, "下载资源链接为空!") }
            return
        }
        handler.post { showToast(hookActivity, "正在下载, 请稍后..") }
        Thread {
            var count = 0
            urls.forEachIndexed { index, url ->
                val absFilename = "$index.${filename.getFileSuffix()}"
                val download = GHttpUtils.download(url, path, absFilename) { real, total -> }
                if (download) count += 1
            }
            handler.post {
                val fail = urls.size - count
                showToast(hookActivity, if (fail <= 0) "下载成功!" else "${fail}个文件下载失败!")
            }
        }.start()
    }

    /// 表情图片
    private fun hookEmoji(hookActivity: AbsActivity) {
        if (!config.isSaveEmojiValue) {
            handler.post { showToast(hookActivity, "未开启长按保存表情图片") }
            return
        }
        //按道理说, 以下两个方法内部都是通用的, 但是无法避免抖音后期更新可能照成布局改动, 因此分开写
        //hookGifEmojiOld(hookActivity)
        //hookImageDetailOld(hookActivity)

        hookGifEmoji(hookActivity)
        hookImageDetail(hookActivity)
    }

    // 评论区 -> 表情详情查看页 GifEmojiDetailActivity
    private fun hookGifEmojiOld(hookActivity: AbsActivity) {
        if (hookActivity is GifEmojiDetailActivity) {
            handler.post { showToast(hookActivity, "查看表情") }
            //获取到屏幕上的布局, (统一ID: android.R.id.content, 也就是 setContentView 设置的布局)
            val contentView = hookActivity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
            //获取到该布局下的 所有ImageView
            val findViews = GViewUtils.findViews(contentView, ImageView::class.java)
            //最后一个是表情图片, 类名(RemoteImageView), 后期可能发生变动
            val remoteImageView = findViews.last()
            //handler.post { GLogUtils.xLogAndToast(hookActivity, "ImageViewType: ${remoteImageView::class.java.name}") }
            if (remoteImageView::class.java.name.contains("RemoteImageView")) { //这里需要注意, 后期抖音可能混淆该类
                remoteImageView.isLongClickable = true
                remoteImageView.setOnLongClickListener {
                    // 获取表情内容
                    val drawable = remoteImageView.drawable
                    val bitmap = drawable.toBitmap(width = drawable.bounds.width(), height = drawable.bounds.height())
                    savePicture(hookActivity, bitmap)
                    true
                }
            }
        }
    }

    private fun hookGifEmoji(hookActivity: AbsActivity) {
        if (hookActivity is GifEmojiDetailActivity) {
            handler.post { showToast(hookActivity, "浏览表情") }
            val gifEmojiExtra = hookActivity.intent.getSerializableExtra("gif_emoji")
            call(gifEmojiExtra) {
                val emoji = gifEmojiExtra as Emoji
                val animateUrl = emoji.animateUrl
                call(animateUrl) {
                    val urlList = animateUrl.urlList
                    if (urlList.isNotEmpty()) {
                        val gifEmojiUrl = urlList.last()
                        //handler.post { GLogUtils.xLogAndToast(hookActivity, "表情URL: $gifEmojiUrl") }
                        // 获取到屏幕上的布局, (统一ID: android.R.id.content, 也就是 setContentView 设置的布局)
                        val contentView = hookActivity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
                        // 获取到该布局下的 所有ImageView
                        val findViews = GViewUtils.findViews(contentView, ImageView::class.java)
                        // 最后一个是表情图片, 类名(RemoteImageView), 后期可能发生变动
                        val remoteImageView = findViews.last()
                        // handler.post { GLogUtils.xLogAndToast(hookActivity, "ImageViewType: ${remoteImageView::class.java.name}") }
                        if (remoteImageView::class.java.name.contains("RemoteImageView")) { //这里需要注意, 后期抖音可能混淆该类
                            remoteImageView.isLongClickable = true
                            remoteImageView.setOnLongClickListener {
                                //表情统一用gif作为后缀
                                downloadEmoji(hookActivity, gifEmojiUrl, "gif")
                                true
                            }
                        }
                    }
                }
            }

        }
    }

    // 评论区 -> 图片详情查看页 ImageDetailActivity
    private fun hookImageDetailOld(hookActivity: AbsActivity) {
        if (hookActivity is ImageDetailActivity) {
            handler.post { showToast(hookActivity, "查看表情") }
            //获取到屏幕上的布局, (统一ID: android.R.id.content, 也就是 setContentView 设置的布局)
            val contentView = hookActivity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
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
                    savePicture(hookActivity, bitmap)
                    true
                }
            }
        }
    }

    private fun hookImageDetail(hookActivity: AbsActivity) {
        if (hookActivity is ImageDetailActivity) {
            handler.post { showToast(hookActivity, "浏览图片") }
            val commentImageExtra = hookActivity.intent.getSerializableExtra("key_image")
            call(commentImageExtra) {
                val commentImage = commentImageExtra as CommentImageStruct
                val originUrl = commentImage.originUrl
                call(originUrl) {
                    val urlList = originUrl.urlList
                    if (urlList.isNotEmpty()) {
                        val commentImageUrl = urlList.first()
                        //handler.post { GLogUtils.xLogAndToast(hookActivity, "图片URL: $commentImageUrl") }
                        //获取到屏幕上的布局, (统一ID: android.R.id.content, 也就是 setContentView 设置的布局)
                        val contentView = hookActivity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
                        //获取到该布局下的 所有ImageView
                        val findViews = GViewUtils.findViews(contentView, ImageView::class.java)
                        //最后一个是表情图片, 类名(RemoteImageView), 后期可能发生变动
                        val remoteImageView = findViews.last()
                        if (remoteImageView::class.java.name.contains("RemoteImageView")) {
                            remoteImageView.isLongClickable = true
                            remoteImageView.setOnLongClickListener {
                                var suffix = "png"
                                if (commentImageUrl.lowercase(Locale.getDefault()).contains(".gif")) {
                                    suffix = "gif"
                                } else if (commentImageUrl.lowercase(Locale.getDefault()).contains(".jpg")) {
                                    suffix = "jpg"
                                } else if (commentImageUrl.lowercase(Locale.getDefault()).contains(".jpeg")) {
                                    suffix = "jpeg"
                                }
                                downloadEmoji(hookActivity, commentImageUrl, suffix)
                                true
                            }
                        }
                    }
                }
            }
        }
    }

    // 保存图片到本地
    private fun savePicture(hookActivity: AbsActivity, bitmap: Bitmap) {
        // 保存路径
        var path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
        // 如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookActivity, "Emoji")
        }

        Thread {
            //以时间戳作为文件名
            val result = GBitmapUtils.bitmap2Path(bitmap, path.absolutePath, "${System.currentTimeMillis()}.png")
            handler.post {
                showToast(hookActivity, "保存${if (result) "成功^_^" else "失败~_~"}!")
            }
        }.start()
    }

    // 下载表情
    private fun downloadEmoji(hookActivity: AbsActivity, url: String, suffix: String) {
        // 保存路径
        var path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
        // 如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            path = ModuleConfig.getModuleDirectory(hookActivity, "Emoji")
        }
        Thread {
            //GLogUtils.xLog("保存: $url")
            // 以时间戳作为文件名
            val download = GHttpUtils.download(url, path.absolutePath, "${System.currentTimeMillis()}.$suffix") { real, total -> }
            handler.post {
                showToast(hookActivity, "保存${if (download) "成功^_^" else "失败~_~"}!")
            }
        }.start()
    }

    private fun File.addChildDir(dirname: String): File {
        val file = File(this, dirname)
        if (file.exists() && file.isDirectory) return file
        if (file.mkdirs()) return file
        return this
    }

    private fun String.getFileSuffix(): String {
        if (this.contains(".")) {
            return this.substring(this.indexOf(".") + 1)
        }
        return this
    }
}