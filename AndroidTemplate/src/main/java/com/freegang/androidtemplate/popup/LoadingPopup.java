package com.freegang.androidtemplate.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;

import com.freegang.androidtemplate.base.popup.BasePopupWindow;
import com.freegang.androidtemplate.databinding.LayoutLoadingPopupBinding;

/**
 * 加载 PopupWindow
 */
public class LoadingPopup extends BasePopupWindow {
    private Context mContext;
    private LayoutLoadingPopupBinding mBinding;

    private int backgroundColor = Color.parseColor("#ccFFFFFF");
    private int loadingBarColor = Color.parseColor("#ff409EFF");
    private String loadingMessage = "拼命加载中";

    //记录上一次按下的时间戳
    private long lastOnBackPressTimer = 0L;
    private String dismissMessage = "再按一次取消";
    private boolean dismissConfirm = true;

    private LoadingDismissCallback loadingDismissCallback;

    public LoadingPopup(Context context) {
        super(context, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        this.mContext = context;
        mBinding = LayoutLoadingPopupBinding.inflate(LayoutInflater.from(context));
        setContentView(null); //直接写死
    }

    @Override
    protected void initPopup(Context context) {
        setClippingEnabled(false); //扩展到状态栏
        setAnimationStyle(android.R.style.Animation_Dialog);
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(mBinding.getRoot());
    }

    @Override
    protected void onShowing() {
        super.onShowing();
        //设置加载背景颜色
        mBinding.getRoot().setBackgroundColor(backgroundColor);
        //设置加载默认颜色
        mBinding.loadingPopupBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(loadingBarColor, PorterDuff.Mode.SRC_IN));
        //设置加载默认文本
        mBinding.loadingPopupText.setText(loadingMessage);
    }

    @Override
    public void dismiss() {
        //再按一次取消
        if ((System.currentTimeMillis() - lastOnBackPressTimer >= 2000L) && dismissConfirm) {
            lastOnBackPressTimer = System.currentTimeMillis();
            Toast.makeText(mContext, dismissMessage, Toast.LENGTH_SHORT).show();
        } else {
            if (mContext != null) mContext = null;
            if (mBinding != null) mBinding = null;
            super.dismiss();
            //回调onDismiss
            call(loadingDismissCallback, LoadingDismissCallback::onDismiss);
        }
    }

    public void dismissNow() {
        if (mContext != null) mContext = null;
        if (mBinding != null) mBinding = null;
        super.dismiss();
        //回调onDismiss
        call(loadingDismissCallback, LoadingDismissCallback::onDismiss);
    }

    /**
     * 延迟一定时间后自动销毁
     *
     * @param delay 延迟时间(毫秒)
     */
    public void showDelay(long delay) {
        showDelay(Gravity.CENTER, delay);
    }

    /**
     * 延迟一定时间后自动销毁
     *
     * @param gravity 显示位置
     * @param delay   延迟时间(毫秒)
     */
    public void showDelay(@PopupWindowGravity int gravity, long delay) {
        super.show(gravity);
        if (delay < 0) {
            throw new IllegalArgumentException("please refer to: `delay > 0`.");
        }
        new Handler(Looper.getMainLooper()).postDelayed(this::dismissNow, delay);
    }

    /**
     * 加载时显示的背景颜色
     *
     * @param backgroundColor
     */
    public LoadingPopup setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 加载滚动条颜色
     *
     * @param loadingBarColor
     */
    public LoadingPopup setLoadingBarColor(@ColorInt int loadingBarColor) {
        this.loadingBarColor = loadingBarColor;
        return this;
    }

    /**
     * 加载提示文本
     *
     * @param loadingMessage
     */
    public LoadingPopup setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
        return this;
    }

    /**
     * 取消文本提示
     *
     * @param message
     */
    public LoadingPopup setDismissMessage(String message) {
        this.dismissMessage = message;
        return this;
    }

    /**
     * 是否需要二次确认, 当 showDelay 调用时, 该属性无效
     *
     * @param dismissConfirm
     */
    public LoadingPopup setDismissConfirm(boolean dismissConfirm) {
        this.dismissConfirm = dismissConfirm;
        return this;
    }

    /**
     * 销毁callback
     *
     * @param loadingDismissCallback
     * @return
     */
    public LoadingPopup setLoadingDismissCallback(LoadingDismissCallback loadingDismissCallback) {
        this.loadingDismissCallback = loadingDismissCallback;
        return this;
    }

    /**
     * 销毁回调
     */
    public interface LoadingDismissCallback {
        void onDismiss();
    }

    /**
     * Deprecated
     * <p>
     * Not recommended for use.
     * <p>
     * If you need to set the background color of the loading view, see at `setBackgroundColor`
     *
     * @param background
     */
    @Deprecated
    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
    }
}
