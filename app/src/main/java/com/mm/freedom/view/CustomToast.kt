package com.mm.freedom.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes

class CustomToast(context: Context, handler: Handler) {
    private var mCanceled = true
    private val mHandler: Handler
    private val mContext: Context
    private val mToast: Toast

    constructor(context: Context) : this(context, Handler(Looper.getMainLooper()))

    init {
        mContext = context
        mHandler = handler
        mToast = Toast.makeText(mContext, null, Toast.LENGTH_SHORT)
        mToast.setGravity(Gravity.BOTTOM, 0, 0)
    }

    val isShowing: Boolean
        get() = !mCanceled

    /**
     * @param resId 要显示的String资源
     * @param duration 显示的时间长
     * 根据LENGTH_MAX进行判断
     * 如果不匹配，进行系统显示
     * 如果匹配，永久显示，直到调用hide()
     */
    fun show(@StringRes resId: Int, duration: Int) {
        mToast.setText(resId)
        if (duration != LENGTH_MAX) {
            mToast.duration = duration
            mToast.show()
        } else if (mCanceled) {
            mToast.duration = Toast.LENGTH_LONG
            mCanceled = false
            showUntilCancel()
        }
    }

    /**
     * @param text 要显示的内容
     * @param duration 显示的时间长
     * 根据LENGTH_MAX进行判断
     * 如果不匹配，进行系统显示
     * 如果匹配，永久显示，直到调用hide()
     */
    fun show(text: CharSequence, duration: Int) {
        mToast.setText(text)
        if (duration != LENGTH_MAX) {
            mToast.duration = duration
            mToast.show()
        } else {
            if (mCanceled) {
                mToast.duration = Toast.LENGTH_LONG
                mCanceled = false
                showUntilCancel()
            }
        }
    }

    /**
     * 隐藏Toast
     */
    fun hide() {
        mToast.cancel()
        mCanceled = true
    }

    private fun showUntilCancel() {
        if (mCanceled) return
        mToast.show()
        mHandler.postDelayed({ showUntilCancel() }, 3000)
    }

    companion object {
        const val LENGTH_MAX = -1
    }
}