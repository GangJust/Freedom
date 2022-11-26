package com.freegang.androidtemplate.base.recycler;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Adapter 刷新通知扩展
 */
public interface AdapterExpand<B extends BaseItem> {

    /**
     * 第一个可见视图位置
     *
     * @return
     */
    int getFirstVisibleItemViewPosition();

    /**
     * 最后一个可见视图位置
     *
     * @return
     */
    int getLastVisibleItemViewPosition();

    /**
     * 获取列表中被渲染的, 最大可见项数量(可以是半个)
     *
     * @return
     */
    double getScreenMaxVisibleViewCount();

    /**
     * 获取当前页页码
     *
     * @return
     */
    int getPageNum();

    /**
     * 跳转到某项
     *
     * @param position
     */
    void scrollToPosition(int position);

    /**
     * 跳转到顶部
     */
    void scrollToTop();

    /**
     * 跳转到底部
     */
    void scrollToBottom();

    /**
     * 跳转到下一页
     */
    void scrollToPrePage();

    /**
     * 跳转到下一页
     */
    void scrollToNextPage();

    /**
     * 滑动到某项
     *
     * @param position
     */
    void smoothScrollToPosition(int position);

    /**
     * 滑动到上一页
     */
    void smoothScrollToPrePage();

    /**
     * 滑动到下一页
     */
    void smoothScrollToNextPage();

    /**
     * 偏移到指定位置
     *
     * @param offsetX
     * @param offsetY
     */
    void smoothOffset(int offsetX, int offsetY);

    /**
     * 滑动到顶部
     */
    void smoothScrollToTop();

    /**
     * 滑动到底部
     */
    void smoothScrollToBottom();

    /**
     * 清空数据
     */
    void clear();

    /**
     * 更新数据
     */
    void refresh();

    /**
     * 更新某项
     *
     * @param position
     */
    void refreshItem(int position);

    /**
     * 批量刷新, 通过position
     *
     * @param positions
     */
    void refreshItems(int... positions);

    /**
     * 更新具有相同key的所有项
     *
     * @param key
     */
    void refreshItem(String key);

    /**
     * 批量刷新, 通过key
     *
     * @param keys
     */
    void refreshItems(B... keys);

    /**
     * 重新设置数据源并刷新
     *
     * @param dataList
     */
    void setItemsAndRefresh(List<B> dataList);

    /**
     * 按照当前列表的默认顺序, 倒转列表
     */
    void reverse();

    /**
     * 随机打乱列表
     */
    void shuffle();

    /**
     * 列表升序
     */
    void ascending();

    /**
     * 列表降序
     */
    void descending();

    /**
     * 按照指定逻辑排序
     *
     * @param c
     */
    void sort(Comparator<? super B> c);

    /**
     * 随机打乱列表
     *
     * @param random
     */
    void shuffle(Random random);

    /**
     * 新增项
     *
     * @param newItem
     */
    boolean add(B newItem);

    /**
     * 新增多项
     *
     * @param newItems
     * @return
     */
    void add(B... newItems);

    /**
     * 新增项, 到指定位置
     *
     * @param position
     * @param newItem
     */
    void insert(int position, B newItem);

    /**
     * 新增多项, 到指定位置
     *
     * @param position
     * @param newItem
     */
    void insert(int position, B... newItem);

    /**
     * 新增项, 到指定项前
     *
     * @param item
     * @param newItem
     */
    boolean insert(B item, B newItem);

    /**
     * 移除某项, 通过position
     *
     * @param position
     * @return
     */
    B remove(int position);

    /**
     * 移除某些项
     *
     * @param position
     * @return
     */
    boolean remove(int... position);

    /**
     * 移除一项
     *
     * @param item
     */
    boolean remove(B item);

    /**
     * 移除具有相同key的所有项
     *
     * @param key
     * @return
     */
    boolean removeByKey(String key);

    /**
     * 更新某项, 通过position
     *
     * @param position
     * @param newItem
     * @return
     */
    B update(int position, B newItem);

    /**
     * 更新某项, 替换指定item
     *
     * @param oldItem
     * @param newItem
     * @return
     */
    boolean update(B oldItem, B newItem);

    /**
     * 获取某项
     *
     * @param position
     * @return
     */
    B getItem(int position);

    /**
     * 获取具有相同key的所有项
     *
     * @param key
     * @return
     */
    List<B> getItems(String key);

    /**
     * 获取具有相同key的第一项
     *
     * @param key
     * @return
     */
    B getItemsFirst(String key);

    /**
     * 获取某项前面的一项
     * @param position
     * @return
     */
    B getItemPre(int position);

    /**
     * 获取某项后面的一项
     * @param position
     * @return
     */
    B getItemNext(int position);

    /**
     * 点击事件, 接口
     * @param <B>
     */
    interface OnItemClickCallback<B extends BaseItem> {
        void onClick(AdapterExpand<B> adapter, B preItem, B nextItem, B item, int position);
    }

    /**
     * 长按事件, 接口
     * @param <B>
     */
    interface OnItemLongClickCallback<B extends BaseItem> {
        boolean onLongClick(AdapterExpand<B> adapter, B preItem, B nextItem, B item, int position);
    }
}
