package com.mm.freedom.hook.base

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import com.mm.freedom.config.Config
import com.mm.freedom.config.ModuleConfig
import com.mm.freedom.utils.GLockUtils
import com.mm.freedom.xposed.ActivityHelper
import de.robv.android.xposed.callbacks.XC_LoadPackage

abstract class BaseActivityHelper<T : Activity>(lpparam: XC_LoadPackage.LoadPackageParam, private val targetClazz: Class<T>) :
    ActivityHelper<T>(lpparam, targetClazz) {
    private var messageToast: Toast? = null
    private var messageDialog: AlertDialog? = null

    //Message Toast
    protected fun showToast(context: Context, message: String) {
        if (messageToast == null) {
            messageToast = Toast.makeText(context, null, Toast.LENGTH_SHORT)
        }
        messageToast!!.setText(null)
        messageToast!!.setText(message)
        messageToast!!.show()

    }

    //Message Dialog
    protected fun showMessageDialog(context: Context, message: String) {
        if (messageDialog == null) {
            messageDialog = AlertDialog.Builder(context)
                .setTitle("Freedom")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    GLockUtils.fileUnlock(application, ".messageDialog")
                }
                .create()
        }
        messageDialog!!.setMessage(message)
        messageDialog!!.show()
    }

    protected fun lockRunning(lockfileName: String, running: Runnable) {
        GLockUtils.fileLock(application, lockfileName) {
            try {
                running.run()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                GLockUtils.fileUnlock(application, lockfileName)
            }
        }
    }

    protected fun isInstance(obj: Any, clazz: Class<*>): Boolean {
        return clazz.isInstance(obj)
    }
}