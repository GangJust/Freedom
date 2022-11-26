package com.mm.freedom.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.mm.freedom.R
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

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
        initMenu()
    }

    private fun initMenu() {
        //菜单(这么写能动态操作菜单)
        val item = binding!!.toolbar.menu.findItem(R.id.moduleMenuOther)
        item.setOnMenuItemClickListener {
            Toast.makeText(this, "暂无更多操作", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun initEvents() {
        binding!!.moduleGithubSrc.root.setOnClickListener {
            //Toast.makeText(this, "计划进行中", Toast.LENGTH_SHORT).show()
            val intent = Intent().apply {
                action = "android.intent.action.VIEW"
                data = Uri.parse(getString(R.string.module_github_url))
            }
            startActivity(intent)
        }
    }

    //权限获取
    private fun requestStoragePermission() {
        //外置存储读写权限
        val permissions = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        //API11 以上需要额外添加`外置存储管理`权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }

        PermissionX.init(this).permissions(permissions).apply {
            onExplainRequestReason { scope, deniedList ->
                val message = "模块设置需要用到文件读写权限.\n\n不允许也能使用, 但设置项将不会生效."
                scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
            }
            request { allGranted, grantedList, deniedList -> }
        }
    }

    //读取设置
    private fun readConfig() {
        ModuleConfig.getModuleConfig(this@MainActivity) {
            runOnUiThread {
                binding?.customDownloadSwitch?.isChecked = it.isCustomDownloadValue
                binding?.clipDataDetailSwitch?.isChecked = it.isClipDataDetailValue
                binding?.saveEmojiSwitch?.isChecked = it.isSaveEmojiValue
            }
        }
    }

    //保存设置
    private fun saveConfig() {
        val config = Config()
        config.isCustomDownloadValue = binding?.customDownloadSwitch?.isChecked ?: false
        config.isClipDataDetailValue = binding?.clipDataDetailSwitch?.isChecked ?: false
        config.isSaveEmojiValue = binding?.saveEmojiSwitch?.isChecked ?: false
        ModuleConfig.putModuleConfig(this@MainActivity, config)
    }

    //Xposed进行Hook调用, 设置模块状态
    private fun moduleHookSuccessText() {
        val moduleHint = findViewById<TextView>(R.id.moduleHint)
        moduleHint.setText(R.string.module_enabled)
    }
}