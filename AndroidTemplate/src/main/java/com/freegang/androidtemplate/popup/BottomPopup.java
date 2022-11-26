package com.freegang.androidtemplate.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.freegang.androidtemplate.R;
import com.freegang.androidtemplate.base.popup.BasePopupWindow;
import com.freegang.androidtemplate.utils.TemplateUtils;

/**
 * 底部弹层
 */
public class BottomPopup extends BasePopupWindow {

    private RelativeLayout containerView;

    private RelativeLayout defaultBottomContainerView;

    //是否触摸外部收回
    private boolean isDismiss = true;

    //是否显示默认的底部容器
    private boolean hasBottomContainer = false;

    public BottomPopup(Context context) {
        super(context, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        initContainerView();
        initDefaultBottomContainerView();
        setContentView(null);
    }

    private void initContainerView() {
        containerView = new RelativeLayout(getContext());
        containerView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        containerView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        containerView.setOnClickListener(v -> {
            if (isDismiss) {
                dismiss();
            }
        });
    }

    private void initDefaultBottomContainerView() {
        defaultBottomContainerView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        defaultBottomContainerView.setLayoutParams(layoutParams);
        defaultBottomContainerView.setBackgroundResource(R.drawable.popup_bottom_background);
        defaultBottomContainerView.setPadding(
                TemplateUtils.SizeUtil.dp2Px(getContext(), 16),
                TemplateUtils.SizeUtil.dp2Px(getContext(), 16),
                TemplateUtils.SizeUtil.dp2Px(getContext(), 16),
                TemplateUtils.SizeUtil.dp2Px(getContext(), 16)
        );
        defaultBottomContainerView.setElevation(32);
        defaultBottomContainerView.setOnClickListener(v -> {
        }); //避免外部回收冲突
    }

    @Override
    protected void initPopup(Context context) {
        setAnimationStyle(android.R.style.Animation_InputMethod); //键盘弹出动画
    }

    @Override
    public void setContentView(View contentView) {
        if (contentView != null) {
            containerView.removeAllViews();
            if (hasBottomContainer) {
                defaultBottomContainerView.addView(contentView);
                containerView.addView(defaultBottomContainerView);
            } else {
                containerView.addView(contentView);
            }
        }
        super.setContentView(containerView);
    }

    /**
     * 底部显示的视图
     *
     * @param view
     * @return
     */
    public BottomPopup setBottomView(View view) {
        view.setOnClickListener(v -> {
        }); //设置一个空的点击事件, 避免响应 触摸外部收回 事件.
        setContentView(view);
        return this;
    }

    /**
     * 触摸外部是否收回, 默认 true
     *
     * @param isDismiss
     * @return
     */
    public BottomPopup setTouchExternalDismiss(boolean isDismiss) {
        this.isDismiss = isDismiss;
        return this;
    }

    /**
     * 是否要显示默认的底部容器
     *
     * @param hasBottomContainer
     * @return
     */
    public BottomPopup setDefaultBottomContainerView(boolean hasBottomContainer) {
        this.hasBottomContainer = hasBottomContainer;
        return this;
    }

    @Override
    public BasePopupWindow show() {
        return super.show(Gravity.BOTTOM);
    }
}
