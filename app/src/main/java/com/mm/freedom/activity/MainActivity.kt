package com.mm.freedom.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.freegang.androidtemplate.dialog.MessageDialog
import com.gyf.immersionbar.ImmersionBar
import com.mm.freedom.R
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.config.Version
import com.mm.freedom.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var versionConfig: Version.VersionConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        ImmersionBar.with(this).apply {
            statusBarView(binding!!.statusBarView)
            transparentBar()
            statusBarDarkFont(true)
            navigationBarDarkIcon(true)
            autoStatusBarDarkModeEnable(true, 0.2f)
            autoNavigationBarDarkModeEnable(true, 0.2f)
        }.init()
        requestStoragePermission()
        initViews()
        initEvents()
        checkVersion()
    }

    override fun onResume() {
        readConfig()
        super.onResume()
    }

    override fun onStop() {
        saveConfig()
        super.onStop()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun initViews() {
        binding?.saveEmojiSwitch?.visibility = View.GONE
        initMenu(false)
    }

    private fun initMenu(hasUpdate: Boolean) {
        val item = binding!!.toolbar.menu.findItem(R.id.moduleMenuOther).apply {
            actionView = layoutInflater.inflate(R.layout.menu_action_view, null) as ImageView
        }
        val view = item.actionView as ImageView
        //?????????, ??????????????????
        if (hasUpdate) {
            view.apply {
                setImageDrawable(AppCompatResources.getDrawable(this.context, R.drawable.ic_montion_state_badge))
                startAnimation(RotateAnimation(
                    0f,
                    450f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                ).apply {
                    duration = 1000
                    fillAfter = true
                })
            }
        } else {
            view.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_motion_state))
        }
        //
        view.setOnClickListener {
            if (!hasUpdate) {
                if (versionConfig == null) {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                MessageDialog()
                    .setSingleButton(true)
                    .setTitle("???????????? ${versionConfig!!.tagName}")
                    .setContent(versionConfig!!.body)
                    .setConfirm("????????????")
                    .setOnConfirmCallback { it.dismiss() }
                    .show(supportFragmentManager)

                return@setOnClickListener
            }
            showUpdateDialog()
        }
    }

    private fun initEvents() {
        binding!!.moduleGithubSrc.root.setOnClickListener {
            //Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show()
            openBrowse(getString(R.string.module_github_url))
        }
    }

    //????????????
    private fun checkVersion() {
        try {
            val versionName = getCurrentVersionName()
            Version.getRemoteReleasesLatest {
                versionConfig = it
                if (it.tagName.isEmpty() || it.name.isEmpty()) return@getRemoteReleasesLatest
                if (Version.compare(versionName, it.tagName) == 1 || Version.compare(versionName, it.name) == 1) {
                    runOnUiThread {
                        Toast.makeText(application, "???????????????!", Toast.LENGTH_SHORT).show()
                        initMenu(true)
                        showUpdateDialog()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //??????????????????
    private fun showUpdateDialog() {
        val versionConfig = versionConfig ?: return
        MessageDialog()
            .setTitle("???????????????${versionConfig.tagName}!")
            .setContent(versionConfig.body)
            .setConfirm("?????????(??????:1234)")
            .setCancel("Github")
            .setConfirmColor(ContextCompat.getColor(this, R.color.teal_200))
            .setOnConfirmCallback {
                openBrowse(getString(R.string.module_lanzou_url))
            }
            .setOnCancelCallback {
                openBrowse(versionConfig.htmlUrl)
            }
            .show(supportFragmentManager)
    }

    //?????????????????????
    private fun getCurrentVersionName(): String {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }

    //????????????
    private fun requestStoragePermission() {
        //????????????????????????
        val permissions = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        //API11 ????????????????????????`??????????????????`??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }

        PermissionX.init(this).permissions(permissions).apply {
            onExplainRequestReason { scope, deniedList ->
                val message = "??????????????????????????????????????????.\n\n?????????????????????, ???????????????????????????."
                scope.showRequestReasonDialog(deniedList, message, "??????", "??????")
            }
            request { allGranted, grantedList, deniedList -> }
        }
    }

    //????????????
    private fun readConfig() {
        ModuleConfig.getModuleConfig(this@MainActivity) {
            runOnUiThread {
                binding?.customDownloadSwitch?.isChecked = it.isCustomDownloadValue
                binding?.clipDataDetailSwitch?.isChecked = it.isClipDataDetailValue
                //binding?.saveEmojiSwitch?.isChecked = it.isSaveEmojiValue
                binding?.saveEmojiSwitch?.isChecked = false
            }
        }
    }

    //????????????
    private fun saveConfig() {
        val config = Config()
        config.isCustomDownloadValue = binding?.customDownloadSwitch?.isChecked ?: false
        config.isClipDataDetailValue = binding?.clipDataDetailSwitch?.isChecked ?: false
        //config.isSaveEmojiValue = binding?.saveEmojiSwitch?.isChecked ?: false
        config.isSaveEmojiValue = false
        config.versionName = getCurrentVersionName()
        ModuleConfig.putModuleConfig(this@MainActivity, config)
    }

    private fun openBrowse(url: String) {
        val intent = Intent().apply {
            action = "android.intent.action.VIEW"
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun showToast(msg: CharSequence) {
        val toast = Toast.makeText(this, null, Toast.LENGTH_LONG)
        toast.setText(msg)
        toast.show()
    }

    //Xposed??????Hook??????, ??????????????????
    private fun moduleHookSuccessText() {
        val moduleHint = findViewById<TextView>(R.id.moduleHint)
        moduleHint.setText(R.string.module_enabled)
    }
}