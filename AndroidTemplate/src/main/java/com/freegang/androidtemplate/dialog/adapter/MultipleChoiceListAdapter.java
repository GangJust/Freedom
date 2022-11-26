package com.freegang.androidtemplate.dialog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.freegang.androidtemplate.R;
import com.freegang.androidtemplate.base.recycler.AdapterExpand;
import com.freegang.androidtemplate.base.recycler.BaseAdapter;
import com.freegang.androidtemplate.dialog.bean.MultipleChoiceItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 多选数据列表适配器
 */
public class MultipleChoiceListAdapter extends BaseAdapter<MultipleChoiceItem, MultipleChoiceListAdapter.ViewHolder>
        implements Serializable {

    private static final long serialVersionUID = 4154799396068346690L;

    //默认已选择的项, 下标数组, 如果某一个下标不存在, 则不做任何操作.
    private int[] selected;

    //选中项改变状态响应事件
    private OnSelectedListener onSelectedListener;

    public MultipleChoiceListAdapter(ArrayList<MultipleChoiceItem> dataList) {
        super(dataList);
    }

    public void setSelected(int... selected) {
        this.selected = selected;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder callCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.layout_multiple_choice_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void callBindViewHolder(@NonNull ViewHolder holder, @NonNull MultipleChoiceItem item, int position) {
        holder.multipleChoiceItemText.setText(item.getChoiceText());
        //点击, 切换状态
        holder.itemView.setOnClickListener(v -> {
            item.setSelected(!item.getSelected());
            holder.itemView.setSelected(item.getSelected());
            holder.multipleChoiceItemIcon.setVisibility(item.getSelected() ? View.VISIBLE : View.INVISIBLE);
            refresh();

            //选中项改变状态响应事件
            call(onSelectedListener, it -> onSelectedListener.onSelected(this, item, position));
        });
        //如果有默认选项, 设置默认选项
        if (selected != null && selected.length != 0) {
            for (int sel : selected) {
                if (sel < 0 || sel >= getItemCount()) continue; //如果下标越界, 则跳过
                getItems().get(sel).setSelected(true);
                holder.multipleChoiceItemIcon.setVisibility(getItems().get(sel).getSelected() ? View.VISIBLE : View.INVISIBLE);
            }
            //selected置空, 确保滚动或者刷新后不再重复进行默认值设置
            selected = null;
        }

        //初始化
        holder.itemView.setSelected(item.getSelected());
        holder.multipleChoiceItemIcon.setVisibility(item.getSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    protected static class ViewHolder extends BaseAdapter.ViewHolder {
        private TextView multipleChoiceItemText;
        private ImageView multipleChoiceItemIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void init() {
            multipleChoiceItemText = itemView.findViewById(R.id.multipleChoiceItemText);
            multipleChoiceItemIcon = itemView.findViewById(R.id.multipleChoiceItemIcon);
        }
    }

    /// 获取被选中的所有项
    public CurrentMultipleChoiceItems getSelected() {
        ArrayList<MultipleChoiceItem> choiceItems = new ArrayList<>();
        ArrayList<Integer> choiceIndexes = new ArrayList<>();

        //遍历
        List<MultipleChoiceItem> dataList = getItems();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getSelected()) {
                choiceItems.add(dataList.get(i));
                choiceIndexes.add(i);
            }
        }
        return new CurrentMultipleChoiceItems(this, choiceItems, choiceIndexes);
    }

    ///选中项统一封装
    public static class CurrentMultipleChoiceItems {
        public final AdapterExpand notify;
        public final ArrayList<MultipleChoiceItem> choiceItems;
        public final ArrayList<Integer> choiceIndexes;

        public CurrentMultipleChoiceItems(AdapterExpand notify, ArrayList<MultipleChoiceItem> choiceItems, ArrayList<Integer> choiceIndexes) {
            this.notify = notify;
            this.choiceItems = choiceItems;
            this.choiceIndexes = choiceIndexes;
        }
    }

    public interface OnSelectedListener {
        void onSelected(AdapterExpand notify, MultipleChoiceItem item, int position);
    }
}
