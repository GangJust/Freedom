package com.mm.freedom.hook.douyin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import com.bytedance.ies.uikit.base.AbsActivity
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.hook.base.BaseActivityHelper
import com.mm.freedom.utils.GHttpUtils
import com.mm.freedom.utils.GJSONUtils
import com.mm.freedom.utils.GPathUtils
import com.ss.android.ugc.aweme.detail.ui.DetailActivity
import com.ss.android.ugc.aweme.main.MainActivity
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

/**
 * 抖音基础类, 基本上所有的Activity类都是继承至它的, 至少目前用到的所有类
 */
class HAbsActivity(lpparam: XC_LoadPackage.LoadPackageParam) :
    BaseActivityHelper<AbsActivity>(lpparam, AbsActivity::class.java) {
    private lateinit var config: Config
    private var onPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onBeforeResume(hookActivity: AbsActivity) {
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
                    // 视频/图片详情获取操作
                    CoroutineScope(Dispatchers.Main).launch {
                        //跳过直播链接, 按文本检查
                        if (shareText.contains("【抖音】") && shareText.contains("正在直播") && shareText.contains("一起支持")) {
                            handler.post { showToast(hookActivity, "不支持直播视频") }
                            return@launch
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
                        // 获取真实地址
                        val originalUrl = getOriginalUrl(sortUrl)
                        if (originalUrl.isEmpty()) {
                            handler.post { showToast(hookActivity, "没有获取到该视频的真实地址") }
                            return@launch
                        }
                        // 获取视频id
                        val videoId = getVideoId(originalUrl)
                        if (videoId.isEmpty()) {
                            handler.post { showToast(hookActivity, "没有获取到该视频的ID") }
                            return@launch
                        }
                        // 解析视频
                        val itemInfo = getItemInfo(videoId)
                        parseItemInfo(hookActivity, itemInfo)
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
    private suspend fun getOriginalUrl(sortUrl: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext GHttpUtils.getRedirectsUrl(sortUrl)
        }
    }

    //获取视频ID
    private fun getVideoId(originalUrl: String): String {
        //正则表达式, 获取视频ID
        val compile = Pattern.compile("(video/)([0-9]+)(/?)") //最后一个斜线可能没有
        val matcher = compile.matcher(originalUrl)
        return if (matcher.find() && matcher.groupCount() >= 1) matcher.group(2) ?: "" else ""
    }

    //通过视频ID解析出帖子(抖音中一条视频就是一个帖子)信息, 返回一个JSON数据
    private suspend fun getItemInfo(videoId: String): String {
        return withContext(Dispatchers.IO) {
            // 旧方案已经无法使用
            //val itemInfoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$videoId"

            // thanks for: https://github.com/Evil0ctal/Douyin_TikTok_Download_API
            val itemInfoUrl =
                "https://www.iesdouyin.com/aweme/v1/web/aweme/detail/?aid=1128&version_name=23.5.0&device_platform=android&os_version=2333&aweme_id=$videoId"
            return@withContext GHttpUtils.get(itemInfoUrl)
        }
    }

    //解析 视频、音频、图片(旧方案)
    private fun parseItemInfoOld(hookActivity: AbsActivity, itemJson: String) {
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
                showOptionDialog(hookActivity, shareTitle, "", "", optionNames.toTypedArray(), optionUrls.toTypedArray())
            } else {
                handler.post { showToast(hookActivity, "没有获取到这条视频的基本信息") }
            }
        } else {
            handler.post { showToast(hookActivity, "没有获取到这条视频的基本信息") }
        }
    }

    //解析 视频、音频、图片(新方案)
    private fun parseItemInfo(hookActivity: AbsActivity, itemJson: String) {
        //解析出 标题、视频、背景音乐
        if (itemJson.isNotEmpty()) {
            val parse = GJSONUtils.parse(itemJson)
            val awemeDetail = GJSONUtils.get(parse, "aweme_detail")
            if (GJSONUtils.isNotEmpty(awemeDetail)) {
                //操作标题
                val optionNames = mutableListOf<String>()
                //操作URL, 和[optionNames]长度相同
                val optionUrls = mutableListOf<String>()

                //分享标题: aweme_detail -> preview_title
                val previewTitle = GJSONUtils.getString(awemeDetail, "preview_title")

                //获取 用户昵称(账号) : author -> nickname / unique_id
                val author = GJSONUtils.get(awemeDetail, "author")
                val nickname = GJSONUtils.getString(author, "nickname")
                val uniqueId = GJSONUtils.getString(author, "unique_id")
                val shortId = uniqueId.ifEmpty { GJSONUtils.getString(author, "short_id") }

                //获取图片(如果有图片, 则图片下载项, 否则显示视频下载项)
                if (!GJSONUtils.isNull(awemeDetail, "images")) {
                    optionNames.add("图片")
                    //图片url: aweme_detail -> images -> url_list[]
                    var urls = ""
                    val images = GJSONUtils.getArray(awemeDetail, "images")
                    for (i in 0 until images.length()) {
                        val imageItem = GJSONUtils.get(images, i)
                        val urlList = GJSONUtils.getArray(imageItem, "url_list")
                        urls = "${urls}|分割|${GJSONUtils.getString(urlList, urlList.length() - 1)}"
                    }
                    optionUrls.add(urls)

                } else if (GJSONUtils.hasKey(awemeDetail, "video")) {
                    //视频url: aweme_detail -> video -> play_addr -> url_list -> 末尾项
                    val urlList = GJSONUtils.getArrayUntil(awemeDetail, "video", "play_addr", "url_list")
                    val videoUrl = GJSONUtils.getString(urlList, urlList.length() - 1)
                    optionNames.add("视频")
                    optionUrls.add(videoUrl)
                }

                //获取音频
                if (GJSONUtils.hasKey(awemeDetail, "music")) {
                    //音频url: aweme_detail -> music -> play_url -> url_list -> 末尾项
                    val urlList = GJSONUtils.getArrayUntil(awemeDetail, "music", "play_url", "url_list")
                    val musicUrl = GJSONUtils.getString(urlList, urlList.length() - 1)
                    optionNames.add("背景音乐")
                    optionUrls.add(musicUrl)
                }

                //构建弹层, 并显示
                showOptionDialog(hookActivity, previewTitle, nickname, shortId, optionNames.toTypedArray(), optionUrls.toTypedArray())
            } else {
                handler.post { showToast(hookActivity, "未获取到基本信息, 新方案暂不支持图文") }
            }
        } else {
            handler.post { showToast(hookActivity, "未获取到基本信息") }
        }
    }

    //显示操作弹窗
    private fun showOptionDialog(
        hookActivity: AbsActivity,
        shareTitle: String,
        nickname: String,
        uniqueId: String,
        optionNames: Array<String>,
        optionUrls: Array<String>,
    ) {
        val builder = AlertDialog.Builder(hookActivity)
        builder.setTitle("Freedom")
        builder.setItems(optionNames) { dialog, which ->
            if (optionUrls[which].isEmpty()) {
                handler.post { showToast(hookActivity, "没有获取到下载地址") }
                return@setItems
            }

            when (which) {
                0 -> {
                    if (optionNames.filter { it.contains("图片") }.size == 1) {
                        val urls = optionUrls[which].split("|分割|")
                        downloadPicture(hookActivity, "$nickname($uniqueId)", urls, shareTitle)
                    } else {
                        downloadVideo(hookActivity, "$nickname($uniqueId)", optionUrls[which], "$shareTitle.mp4")
                    }
                }
                1 -> downloadMusic(hookActivity, "$nickname($uniqueId)", optionUrls[which], "$shareTitle.mp3")
            }
            dialog.dismiss()
        }
        handler.post { builder.show() }
    }

    // 下载视频
    private fun downloadVideo(hookActivity: AbsActivity, authorName: String, url: String, filename: String) {
        if (url.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到这条视频的URL地址") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Video/";
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            val path = ModuleConfig.getModuleDirectory(hookActivity, "Video").addChildDir(authorName)
            downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
        } else {
            val path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
            downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
        }
    }

    // 下载音频
    private fun downloadMusic(hookActivity: AbsActivity, authorName: String, url: String, filename: String) {
        if (url.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到这条视频的背景音乐") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Music/";
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            val path = ModuleConfig.getModuleDirectory(hookActivity, "Music").addChildDir(authorName)
            downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
        } else {
            val path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_MUSIC)
            downloadFileAndShowDialog(hookActivity, url, path.absolutePath, filename)
        }
    }

    // 下载图片(下载图片时filename作为文件夹)
    private fun downloadPicture(hookActivity: AbsActivity, authorName: String, urls: List<String>, filename: String) {
        //如果没有文件名以时间戳为文件名, 否则替换特殊字符
        val filename = if (filename.isEmpty()) "${System.currentTimeMillis() / 1000}" else GPathUtils.replaceSpecialChars(filename)
        if (urls.isEmpty()) {
            handler.post { showToast(hookActivity, "没有获取到图片") }
            return
        }
        //String path = hookActivity.getExternalFilesDir(null) + "/Picture/";
        //如果打开了下载到Freedom文件夹开关
        if (config.isCustomDownloadValue) {
            val path = ModuleConfig.getModuleDirectory(hookActivity, "Picture").addChildDir(authorName).addChildDir(filename)
            downloadFiles(hookActivity, urls, path.absolutePath, "$filename.jpeg")
        } else {
            val path = File(GPathUtils.getStoragePath(hookActivity), Environment.DIRECTORY_DCIM)
            downloadFiles(hookActivity, urls, path.absolutePath, "${System.currentTimeMillis() / 1000}.jpeg")
        }
    }

    // 通用弹窗下载文件
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
            //如果没有文件名以时间戳为文件名, 否则替换特殊字符
            val filename = if (filename.isEmpty()) "${System.currentTimeMillis() / 1000}" else GPathUtils.replaceSpecialChars(filename)
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

    // 通用下载多个文件
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