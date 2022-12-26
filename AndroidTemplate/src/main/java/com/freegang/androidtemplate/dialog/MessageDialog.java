package com.freegang.androidtemplate.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.freegang.androidtemplate.R;
import com.freegang.androidtemplate.base.dialog.BaseDialog;
import com.freegang.androidtemplate.databinding.LayoutMessageDialogBinding;

/**
 * 普通消息Dialog
 */
public class MessageDialog extends BaseDialog<LayoutMessageDialogBinding, MessageDialog.MessageDialogEntity> {
    private MessageDialogEntity entity = new MessageDialogEntity();

    /**
     * 标题内容
     *
     * @param title
     */
    public MessageDialog setTitle(CharSequence title) {
        this.entity.title = title;
        return this;
    }

    /**
     * 标题颜色
     *
     * @param titleColor
     * @return
     */
    public MessageDialog setTitleColor(int titleColor) {
        this.entity.titleColor = titleColor;
        return this;
    }

    /**
     * 标题是否居中
     *
     * @param centerTitle
     */
    public MessageDialog setCenterTitle(boolean centerTitle) {
        this.entity.centerTitle = centerTitle;
        return this;
    }

    /**
     * 消息正文内容
     *
     * @param content
     */
    public MessageDialog setContent(CharSequence content) {
        this.entity.content = content;
        return this;
    }

    /**
     * 消息正文颜色
     *
     * @param contentColor
     */
    public MessageDialog setContentColor(int contentColor) {
        this.entity.contentColor = contentColor;
        return this;
    }

    /**
     * 取消按钮, 文本内容
     *
     * @param cancel
     */
    public MessageDialog setCancel(CharSequence cancel) {
        this.entity.cancel = cancel;
        return this;
    }

    /**
     * 取消按钮, 文本颜色
     *
     * @param cancelColor
     */
    public MessageDialog setCancelColor(int cancelColor) {
        this.entity.cancelColor = cancelColor;
        return this;
    }

    /**
     * 确认按钮, 文本内容
     *
     * @param confirm
     */
    public MessageDialog setConfirm(CharSequence confirm) {
        this.entity.confirm = confirm;
        return this;
    }

    /**
     * 确认按钮, 文本颜色
     *
     * @param confirmColor
     */
    public MessageDialog setConfirmColor(int confirmColor) {
        this.entity.confirmColor = confirmColor;
        return this;
    }

    /**
     * 是否反正操作按钮颜色
     *
     * @param reversalColor
     * @return
     */
    public MessageDialog setReversalColor(boolean reversalColor) {
        this.entity.isReversalColor = reversalColor;
        return this;
    }

    /**
     * 是否是单按钮, 如果为 true 则只会响应 confirm 相关的设置
     *
     * @param singleButton
     */
    public MessageDialog setSingleButton(boolean singleButton) {
        this.entity.isSingleButton = singleButton;
        return this;
    }

    /**
     * 取消按钮点击, 回调事件
     *
     * @param onMessageDialogCancelCallback
     */
    public MessageDialog setOnCancelCallback(OnCancelCallback onMessageDialogCancelCallback) {
        this.entity.onMessageDialogCancelCallback = onMessageDialogCancelCallback;
        return this;
    }

    /**
     * 确定按钮点击, 回调事件
     *
     * @param onMessageDialogConfirmCallback
     */
    public MessageDialog setOnConfirmCallback(OnConfirmCallback onMessageDialogConfirmCallback) {
        this.entity.onMessageDialogConfirmCallback = onMessageDialogConfirmCallback;
        return this;
    }

    @Override
    protected LayoutMessageDialogBinding callCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutMessageDialogBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView(@NonNull LayoutMessageDialogBinding binding) {
        binding.messageDialogTitle.setText(this.entity.title);
        binding.messageDialogTitle.setTextColor(this.entity.titleColor);
        binding.messageDialogTitle.setTextAlignment(this.entity.centerTitle ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_INHERIT);

        binding.messageDialogContent.setText(this.entity.content);
        binding.messageDialogContent.setTextColor(this.entity.contentColor);

        binding.messageDialogCancel.setText(this.entity.cancel);
        binding.messageDialogCancel.setTextColor(this.entity.cancelColor);

        binding.messageDialogConfirm.setText(this.entity.confirm);
        binding.messageDialogConfirm.setTextColor(this.entity.confirmColor);

        // 如果反转操作按钮颜色
        if(entity.isReversalColor){
            binding.messageDialogCancel.setTextColor(this.entity.confirmColor);
            binding.messageDialogConfirm.setTextColor(this.entity.cancelColor);
        }

        // 如果显示单个按钮
        if (this.entity.isSingleButton) {
            binding.messageDialogCancel.setVisibility(View.GONE);
            binding.messageDialogConfirm.setBackgroundResource(R.drawable.dialog_single_button_background);
        }
    }

    @Override
    protected void initEvent(@NonNull LayoutMessageDialogBinding binding) {
        binding.messageDialogCancel.setOnClickListener(v -> {
            call(this.entity.onMessageDialogCancelCallback, it -> it.onCancel(this), this::dismiss);
        });
        binding.messageDialogConfirm.setOnClickListener(v -> {
            call(this.entity.onMessageDialogConfirmCallback, it -> it.onConfirm(this));
        });
    }

    @Override
    protected MessageDialogEntity saveState() {
        return entity;
    }

    @Override
    protected void restoreState(@NonNull MessageDialogEntity entity) {
        this.entity = entity;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, "MessageDialog");
    }

    //取消回调事件
    public interface OnCancelCallback {
        void onCancel(MessageDialog dialog);
    }

    //确定回调事件
    public interface OnConfirmCallback {
        void onConfirm(MessageDialog dialog);
    }

    /// MessageDialog 实体类
    public static class MessageDialogEntity extends BaseDialog.BaseDialogEntity {
        private static final long serialVersionUID = 3121241690534610292L;
        //标题文本
        private CharSequence title = "Tips";
        //标题文本颜色
        private int titleColor = Color.parseColor("#ff333333");
        //标题是否居中
        private boolean centerTitle = false;
        //消息内容
        private CharSequence content = "这是一条消息.";
        //消息内容颜色
        private int contentColor = Color.parseColor("#ff333333");
        //取消文本
        private CharSequence cancel = "取消";
        //取消文本颜色
        private int cancelColor = Color.parseColor("#ff666666");
        //确定文本
        private CharSequence confirm = "确定";
        //确定文本颜色
        private int confirmColor = Color.parseColor("#ff46ADFB");
        //是否翻转操作按钮颜色
        private boolean isReversalColor = false;
        //是否是单个按钮(如果是, 则只响应确定按钮事件)
        private boolean isSingleButton = false;
        //取消回调事件
        private MessageDialog.OnCancelCallback onMessageDialogCancelCallback;
        //确定回调事件
        private MessageDialog.OnConfirmCallback onMessageDialogConfirmCallback;
    }
}
