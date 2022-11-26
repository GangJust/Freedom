package com.freegang.androidtemplate.dialog.layoutmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 超过最大数量后, RecyclerView滚动
 */
public class MaxCountLinearLayoutManager extends LinearLayoutManager {
    private int mMaxCount; //最大数量, 当 maxCount<=0 时, 不做限制

    public MaxCountLinearLayoutManager(Context context) {
        this(context, -1);
    }

    public MaxCountLinearLayoutManager(Context context, int maxCount) {
        super(context);
        this.mMaxCount = maxCount;
    }

    public MaxCountLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int maxCount) {
        super(context, orientation, reverseLayout);
        this.mMaxCount = maxCount;
    }

    public MaxCountLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int maxCount) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMaxCount = maxCount;
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        if (mMaxCount <= 0 || getItemCount() <= mMaxCount || getItemCount() == 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }
        View child = recycler.getViewForPosition(0);
        addView(child);
        measureChildWithMargins(child, 0, 0);
        int itemWidth = getDecoratedMeasuredWidth(child);
        int itemHeight = getDecoratedMeasuredHeight(child);
        removeAndRecycleView(child, recycler);

        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        double width;
        double height;
        if (getOrientation() == RecyclerView.VERTICAL) {
            width = (widthMode == View.MeasureSpec.EXACTLY) ? View.MeasureSpec.getSize(widthSpec) : itemWidth;
            height = itemHeight * mMaxCount;
        } else {
            height = (heightMode == View.MeasureSpec.EXACTLY) ? View.MeasureSpec.getSize(heightMode) : itemHeight;
            width = itemWidth * mMaxCount;
        }

        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        if (getItemCount() < mMaxCount) {
            return super.isAutoMeasureEnabled();
        }
        return false;
    }
}
