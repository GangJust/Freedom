package com.freegang.androidtemplate.dialog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.freegang.androidtemplate.R;
import com.freegang.androidtemplate.base.recycler.AdapterExpand;
import com.freegang.androidtemplate.base.recycler.BaseAdapter;
import com.freegang.androidtemplate.dialog.bean.ChoiceItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 单选数据列表适配器
 */
public class ChoiceListAdapter extends BaseAdapter<ChoiceItem, ChoiceListAdapter.ViewHolder>
        implements Serializable {

    private static final long serialVersionUID = -8598889756385128803L;

    private OnChoiceItemClickCallback onChoiceItemClickCallback;

    public void setOnChoiceItemClickCallback(OnChoiceItemClickCallback onChoiceItemClickCallback) {
        this.onChoiceItemClickCallback = onChoiceItemClickCallback;
    }

    public ChoiceListAdapter(ArrayList<ChoiceItem> dataList) {
        super(dataList);
    }

    @NonNull
    @Override
    public ViewHolder callCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.layout_choice_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void callBindViewHolder(@NonNull ViewHolder holder, @NonNull ChoiceItem item, int position) {
        holder.choiceItemText.setText(item.getChoiceText());
        //点击事件
        holder.itemView.setOnClickListener(v -> {
            call(onChoiceItemClickCallback, it -> {
                it.onChoiceItem(new CurrentChoiceItem(this, item, position));
            });
        });

    }

    protected static class ViewHolder extends BaseAdapter.ViewHolder {
        private TextView choiceItemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void init() {
            choiceItemText = itemView.findViewById(R.id.choiceItemText);
        }
    }

    public static class CurrentChoiceItem {
        public final AdapterExpand notify;
        public final ChoiceItem currentItem;
        public final int currentIndex;

        public CurrentChoiceItem(AdapterExpand notify, ChoiceItem currentItem, int currentIndex) {
            this.notify = notify;
            this.currentItem = currentItem;
            this.currentIndex = currentIndex;
        }
    }

    public interface OnChoiceItemClickCallback {
        void onChoiceItem(CurrentChoiceItem currentChoiceItem);
    }
}
