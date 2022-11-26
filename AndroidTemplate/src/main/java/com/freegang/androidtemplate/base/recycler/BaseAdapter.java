package com.freegang.androidtemplate.base.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freegang.androidtemplate.base.interfaces.TemplateCallDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 通用Adapter基类
 *
 * @param <B>
 * @author Gang
 */
public abstract class BaseAdapter<B extends BaseItem, VH extends BaseAdapter.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements AdapterExpand<B>, TemplateCallDefault {

    private RecyclerView mRecyclerView;

    //数据列表
    private List<B> items;

    //第一个可见项位置
    private int firstItemPosition;

    //最后一个可见项位置
    private int lastItemPosition;

    //页码
    private int pageNum = 1;

    //滚动到指定position后(只要出现在屏幕中,都算定位成功); 因此, 定位后需要该position在第一个位置, 应该再次滚动一次.
    private boolean shouldScroll = false;

    //需要滚动的position
    private int needToPosition = 0;

    protected OnItemClickCallback<B> defaultOnItemClickCallback;

    protected OnItemLongClickCallback<B> defaultOnItemLongClickCallback;

    public BaseAdapter(List<B> items) {
        this.items = items;
    }

    public void setItems(List<B> items) {
        this.items = items;
    }

    public List<B> getItems() {
        return items;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //停止滚动后
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    firstItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
                    lastItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

                    //是否需要将某项滚动到第一项
                    if (shouldScroll) {
                        shouldScroll = false;
                        smoothScrollToPosition(needToPosition);
                    }
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return callCreateViewHolder(inflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        callBindViewHolder(holder, items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setDefaultOnItemClickCallback(OnItemClickCallback<B> defaultOnItemClickCallback) {
        this.defaultOnItemClickCallback = defaultOnItemClickCallback;
    }

    public void setDefaultOnItemLongClickCallback(OnItemLongClickCallback<B> defaultOnItemLongClickCallback) {
        this.defaultOnItemLongClickCallback = defaultOnItemLongClickCallback;
    }

    @NonNull
    public abstract VH callCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType);

    public abstract void callBindViewHolder(@NonNull VH holder, @NonNull B item, int position);

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            init();
        }

        public abstract void init();
    }

    /// extend method
    @Override
    public int getFirstVisibleItemViewPosition() {
        firstItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        return firstItemPosition;
    }

    @Override
    public int getLastVisibleItemViewPosition() {
        lastItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        return lastItemPosition;
    }

    @Override
    public double getScreenMaxVisibleViewCount() {
        int childHeight = mRecyclerView.getChildAt(0).getHeight();
        int height = mRecyclerView.getHeight();
        return height * 1.0 / childHeight;
    }

    @Override
    public int getPageNum() {
        return pageNum + 1;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0) {
            mRecyclerView.scrollToPosition(0);
            return;
        }

        if (position >= getItemCount()) {
            mRecyclerView.scrollToPosition(getItemCount() - 1);
            return;
        }

        int firstItemPosition = getFirstVisibleItemViewPosition();
        int lastItemPosition = getLastVisibleItemViewPosition();
        if (position <= firstItemPosition) {
            mRecyclerView.scrollToPosition(position);
        } else if (position <= lastItemPosition) {
            int top = mRecyclerView.getChildAt(position - firstItemPosition).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(position);
        }
    }

    @Override
    public void scrollToTop() {
        scrollToPosition(0);
    }

    @Override
    public void scrollToBottom() {
        scrollToPosition(getItemCount() - 1);
    }

    @Override
    public void scrollToPrePage() {
        int range = getLastVisibleItemViewPosition() - getFirstVisibleItemViewPosition();
        int prePosition = getFirstVisibleItemViewPosition() - range;
        pageNum = Math.max((prePosition / range), 0);
        scrollToPosition(prePosition < range ? 0 : prePosition); //如果前一页数量不足可见范围, 则直接回顶部
    }

    @Override
    public void scrollToNextPage() {
        int range = getLastVisibleItemViewPosition() - getFirstVisibleItemViewPosition();
        int nextPosition = getLastVisibleItemViewPosition();
        pageNum = nextPosition / range;
        scrollToPosition(nextPosition);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        int firstItemPosition = getFirstVisibleItemViewPosition();
        int lastItemPosition = getLastVisibleItemViewPosition();
        if (position < firstItemPosition) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItemPosition) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItemPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            mRecyclerView.smoothScrollToPosition(position);
            // 再通过onScrollStateChanged控制再次调用smoothScrollToPosition，执行上一个判断中的方法
            shouldScroll = true;
            needToPosition = position;
        }
    }

    @Override
    public void smoothScrollToTop() {
        smoothScrollToPosition(0);
    }

    @Override
    public void smoothScrollToBottom() {
        smoothScrollToPosition(getItemCount() - 1);
    }

    @Override
    public void smoothScrollToPrePage() {
        int range = getLastVisibleItemViewPosition() - getFirstVisibleItemViewPosition();
        int prePosition = getFirstVisibleItemViewPosition() - range;
        pageNum = Math.max((prePosition / range), 1);
        smoothScrollToPosition(prePosition);
    }

    @Override
    public void smoothScrollToNextPage() {
        int range = getLastVisibleItemViewPosition() - getFirstVisibleItemViewPosition();
        int nextPosition = getLastVisibleItemViewPosition();
        pageNum = nextPosition / range;
        smoothScrollToPosition(nextPosition);
    }

    @Override
    public void smoothOffset(int offsetX, int offsetY) {
        mRecyclerView.smoothScrollBy(offsetX, offsetY); //内部会自行判断是横向滚动还是纵向滚动
    }

    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }


    @Override
    public void refreshItem(int position) {
        notifyItemChanged(position);
    }

    @Override
    public void refreshItems(@NonNull int... positions) {
        for (int position : positions) {
            notifyItemChanged(position);
        }
    }

    @Override
    public void refreshItem(String key) {
        for (int j = 0; j < items.size(); j++) {
            String oldKey = items.get(j).getKey();
            if (key.equals(oldKey)) {
                notifyItemChanged(j);
                //break;  //这里不应该break, 因为可能存在多个key相同的情况
            }
        }
    }

    @Override
    public void refreshItems(@NonNull B... keys) {
        for (int i = 0; i < keys.length; i++) {
            String newKay = keys[i].getKey();
            refreshItem(newKay);
        }
    }

    @Override
    public void setItemsAndRefresh(List<B> items) {
        this.items = (items == null) ? new ArrayList<>() : items;
        notifyDataSetChanged();
    }

    @Override
    public void reverse() {
        Collections.reverse(items);
        notifyDataSetChanged();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(items);
        notifyDataSetChanged();
    }

    @Override
    public void ascending() {
        this.sort((o1, o2) -> o1.hashCode() - o2.hashCode());
    }

    @Override
    public void descending() {
        this.sort((o1, o2) -> o2.hashCode() - o1.hashCode());
    }

    @Override
    public void sort(Comparator<? super B> c) {
        Collections.sort(items, c);
        notifyDataSetChanged();
    }

    @Override
    public void shuffle(Random random) {
        Collections.shuffle(items, random);
        notifyDataSetChanged();
    }

    @Override
    public boolean add(B newItem) {
        boolean result = items.add(newItem);
        notifyItemInserted(items.size() - 1);
        return result;
    }

    @Override
    public void add(@NonNull B... newItems) {
        for (B newItem : newItems) {
            items.add(newItem);
            notifyItemInserted(items.size() - 1);
        }
    }

    @Override
    public void insert(int position, B newItem) {
        items.add(position, newItem);
        notifyItemInserted(position);
    }

    @Override
    public void insert(int position, @NonNull B... newItems) {
        for (B newItem : newItems) {
            items.add(position, newItem);
            notifyItemInserted(position++);
        }
    }

    @Override
    public boolean insert(B item, B newItem) {
        int indexOf = items.indexOf(item);
        if (indexOf != -1) {
            items.add(indexOf, newItem);
            notifyItemInserted(indexOf);
            return true;
        }
        return false;
    }

    @Override
    public B remove(int position) {
        if (getItemCount() <= 0) return null;

        B result = items.remove(position);
        notifyItemRemoved(position);
        return result;
    }

    @Override
    public boolean remove(int... positions) {
        if (getItemCount() <= 0) return false;

        for (int i : positions) {
            if (i < 0 || i >= getItemCount()) continue;
            items.remove(i);
            notifyItemRemoved(i);
        }
        return false;
    }

    @Override
    public boolean remove(B item) {
        if (getItemCount() <= 0) return false;

        int indexOf = items.indexOf(item);
        if (indexOf != -1) {
            items.remove(indexOf);
            notifyItemRemoved(indexOf);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeByKey(String key) {
        if (getItemCount() <= 0) return false;

        boolean result = false;
        for (int i = items.size() - 1; i >= 0; i--) {
            String key1 = items.get(i).getKey();
            if (key1.equals(key)) {
                items.remove(i);
                notifyItemRemoved(i);
                result = true;
            }
        }
        return result;
    }

    @Override
    public B update(int position, B newItem) {
        if (getItemCount() <= 0) return null;

        B oldItem = items.get(position);
        items.set(position, newItem);
        notifyItemChanged(position);
        return oldItem;
    }

    @Override
    public boolean update(B oldItem, B newItem) {
        if (getItemCount() <= 0) return false;

        int indexOf = items.indexOf(oldItem);
        if (indexOf != -1) {
            items.set(indexOf, newItem);
            notifyItemChanged(indexOf);
            return true;
        }
        return false;
    }

    @Override
    public B getItem(int position) {
        if (getItemCount() <= 0) return null;
        return items.get(position);
    }

    @Override
    public List<B> getItems(String key) {
        if (getItemCount() <= 0)
            return new ArrayList<>(); //因为列表视图随时可变, 这里千万不要用 Collections.emptyList()

        List<B> resultList = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            String key1 = items.get(index).getKey();
            if (key1.equals(key)) resultList.add(items.get(index));
        }
        return resultList;
    }

    @Override
    public B getItemsFirst(String key) {
        if (getItemCount() <= 0) return null;

        for (int index = 0; index < items.size(); index++) {
            String key1 = items.get(index).getKey();
            if (key1.equals(key)) return items.get(index);
        }

        return null;
    }

    @Override
    public B getItemPre(int position) {
        position = position - 1;
        if (position < 0 || position >= getItemCount()) {
            throw new IndexOutOfBoundsException("`position = " + position + "`, getItemCount() = " + getItemCount());
        }
        return getItems().get(position);
    }

    @Override
    public B getItemNext(int position) {
        position = position + 1;
        if (position < 0 || position >= getItemCount()) {
            throw new IndexOutOfBoundsException("`position = " + position + "`, getItemCount() = " + getItemCount());
        }
        return getItems().get(position);
    }
}
