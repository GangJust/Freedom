package com.freegang.androidtemplate.base.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;

import com.freegang.androidtemplate.base.interfaces.TemplateCallDefault;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public abstract class BasePopupWindow extends PopupWindow
        implements TemplateCallDefault {

    private Context context;

    //父布局, 默认为Android根布局
    protected View parentView;

    public BasePopupWindow(Context context) {
        super(context);
        this.context = context;
        this.parentView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        initPopup(context);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //取消默认背景色(设置透明)
    }

    public BasePopupWindow(Context context, int width, int height) {
        this(context);
        setWidth(width);
        setHeight(height);
    }

    public BasePopupWindow(Context context, int width, int height, boolean focusable) {
        this(context, width, height);
        setFocusable(focusable);
    }

    public BasePopupWindow(View contentView) {
        this(contentView.getContext());
        setContentView(contentView);
    }

    public BasePopupWindow(View contentView, int width, int height) {
        this(contentView);
        setWidth(width);
        setHeight(height);
    }

    public BasePopupWindow(View contentView, int width, int height, boolean focusable) {
        this(contentView.getContext(), width, height);
        setFocusable(focusable);
    }

    protected abstract void initPopup(Context context);

    public Context getContext() {
        return context;
    }

    public View getParentView() {
        return parentView;
    }

    public BasePopupWindow show() {
        return show(parentView);
    }

    public BasePopupWindow show(View parentView) {
        return show(parentView, Gravity.CENTER);
    }

    public BasePopupWindow show(View parentView, @PopupWindowGravity int gravity) {
        return show(parentView, gravity, 0, 0);
    }

    public BasePopupWindow show(@PopupWindowGravity int gravity) {
        return show(gravity, 0, 0);
    }

    public BasePopupWindow show(@PopupWindowGravity int gravity, int offsetX, int offsetY) {
        return show(parentView, gravity, offsetX, offsetY);
    }

    public BasePopupWindow show(View parentView, @PopupWindowGravity int gravity, int offsetX, int offsetY) {
        if (!isShowing()) {
            this.parentView = parentView;
            showAtLocation(parentView, gravity, offsetX, offsetY);
            onShowing();
        }
        return this;
    }


    /**
     * 显示后回调, 可以在这里对默认组件进行修改
     */
    @CallSuper
    protected void onShowing() {
    }

    @CallSuper
    @Override
    public void dismiss() {
        super.dismiss();
        if (parentView != null) {
            parentView = null;
        }
    }

    //定位注解
    @IntDef({
            Gravity.TOP,
            Gravity.BOTTOM,
            Gravity.START,
            Gravity.END,
            Gravity.CENTER_VERTICAL,
            Gravity.FILL_VERTICAL,
            Gravity.CENTER_HORIZONTAL,
            Gravity.FILL_HORIZONTAL,
            Gravity.CENTER,
            Gravity.FILL,
            Gravity.CLIP_VERTICAL,
            Gravity.CLIP_HORIZONTAL,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PopupWindowGravity {
    }
}
