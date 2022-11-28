package com.freegang.androidtemplate.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.freegang.androidtemplate.base.dialog.BaseDialog;
import com.freegang.androidtemplate.databinding.LayoutChoiceDialogBinding;
import com.freegang.androidtemplate.dialog.adapter.ChoiceListAdapter;
import com.freegang.androidtemplate.dialog.bean.ChoiceItem;
import com.freegang.androidtemplate.recycler.divider.ColorDividerItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 单选Dialog
 */
public class ChoiceDialog extends BaseDialog<LayoutChoiceDialogBinding, ChoiceDialog.ChoiceDialogEntity> {
    private ChoiceDialogEntity entity = new ChoiceDialogEntity();

    /**
     * `标题`文本
     *
     * @param title
     * @return
     */
    public ChoiceDialog setTitle(String title) {
        this.entity.title = title;
        return this;
    }

    /**
     * `标题`是否居中
     *
     * @param centerTitle
     * @return
     */
    public ChoiceDialog setCenterTitle(boolean centerTitle) {
        this.entity.centerTitle = centerTitle;
        return this;
    }

    /**
     * `取消`按钮, 文本内容
     *
     * @param cancel
     * @return
     */
    public ChoiceDialog setCancel(String cancel) {
        this.entity.cancel = cancel;
        return this;
    }

    /**
     * `取消`按钮, 文本颜色
     *
     * @param cancelColor
     * @return
     */
    public ChoiceDialog setCancelColor(@ColorInt int cancelColor) {
        this.entity.cancelColor = cancelColor;
        return this;
    }

    /**
     * 设置选项列表
     *
     * @param choiceList
     * @return
     */
    public ChoiceDialog setChoiceList(ArrayList<ChoiceItem> choiceList) {
        this.entity.choiceListAdapter.setItems(choiceList);
        return this;
    }

    /**
     * 设置选项列表
     *
     * @param choiceList
     * @return
     */
    public ChoiceDialog setChoiceList(ChoiceItem... choiceList) {
        setChoiceList(new ArrayList<>(Arrays.asList(choiceList)));
        return this;
    }

    /**
     * `分割线`是否显示
     *
     * @param showDivider 默认 false
     * @return
     */
    public ChoiceDialog setShowDivider(boolean showDivider) {
        this.entity.showDivider = showDivider;
        return this;
    }

    /**
     * `取消`按钮点击, 回调事件
     *
     * @param onCancelCallback
     * @return
     */
    public ChoiceDialog setOnCancelCallback(OnCancelCallback onCancelCallback) {
        this.entity.onCancelCallback = onCancelCallback;
        return this;
    }

    /**
     * `列表项`选中, 回调事件
     *
     * @param onSelectedCallback
     * @return
     */
    public ChoiceDialog setOnSelectedCallback(OnSelectedCallback onSelectedCallback) {
        this.entity.onSelectedCallback = onSelectedCallback;
        return this;
    }

    @Override
    protected LayoutChoiceDialogBinding callCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutChoiceDialogBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView(@NonNull LayoutChoiceDialogBinding binding) {
        binding.choiceDialogTitle.setText(this.entity.title);
        binding.choiceDialogTitle.setTextAlignment(this.entity.centerTitle ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_INHERIT);

        binding.choiceDialogCancel.setText(this.entity.cancel);
        binding.choiceDialogCancel.setTextColor(this.entity.cancelColor);

        binding.choiceDialogList.setAdapter(this.entity.choiceListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        binding.choiceDialogList.setLayoutManager(layoutManager);
        if (this.entity.showDivider) {
            ColorDividerItemDecoration itemDecoration = new ColorDividerItemDecoration(layoutManager.getOrientation());
            binding.choiceDialogList.addItemDecoration(itemDecoration);
        }
    }

    @Override
    protected void initEvent(@NonNull LayoutChoiceDialogBinding binding) {
        binding.choiceDialogCancel.setOnClickListener(v -> {
            call(this.entity.onCancelCallback, it -> it.onCancel(this), this::dismiss);
        });
        //因为是单选, 所以直接响应列表项点击事件
        this.entity.choiceListAdapter.setOnChoiceItemClickCallback(currentChoiceItem -> {
            call(this.entity.onSelectedCallback, it -> {
                it.onSelected(this, currentChoiceItem.currentItem, currentChoiceItem.currentIndex);
            });
        });
    }
    @Override
    protected ChoiceDialogEntity saveState() {
        return entity;
    }

    @Override
    protected void restoreState(@NonNull ChoiceDialogEntity entity) {
        this.entity = entity;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, "ChoiceDialog");
    }

    /// 单选取消回调事件
    public interface OnCancelCallback {
        void onCancel(@NonNull ChoiceDialog dialog);
    }

    /// 单选选中回调事件
    public interface OnSelectedCallback {
        void onSelected(ChoiceDialog dialog, ChoiceItem item, int index);
    }

    /// ChoiceDialogEntity 实体类
    protected static class ChoiceDialogEntity extends BaseDialog.BaseDialogEntity {
        private static final long serialVersionUID = -9034471404444516322L;
        //标题文本
        private String title = "请选择";
        //标题是否居中
        private boolean centerTitle = false;
        //取消文本
        private String cancel = "取消";
        //取消文本颜色
        private int cancelColor = Color.parseColor("#ff46ADFB");
        //是否显示分割线
        private boolean showDivider = false;
        //选项列表
        private ChoiceListAdapter choiceListAdapter = new ChoiceListAdapter(new ArrayList<>());
        //取消回调事件
        private OnCancelCallback onCancelCallback;
        //选项选中回调事件
        private OnSelectedCallback onSelectedCallback;
    }
}
