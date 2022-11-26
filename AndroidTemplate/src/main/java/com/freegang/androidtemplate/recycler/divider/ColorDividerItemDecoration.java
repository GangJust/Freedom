package com.freegang.androidtemplate.recycler.divider;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

/**
 * 可自行指定颜色的 ItemDecoration
 */
public class ColorDividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "ColorDividerItem";
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {}


    private int mColor;
    private int mWidth;
    private int mOrientation;
    private boolean showLast = false;  //最后一项是否显示分割线
    private boolean mOver;  //是否只绘制在图层的最上层

    private final Rect mBounds = new Rect();

    public ColorDividerItemDecoration(@Orientation int orientation) {
        this(Color.parseColor("#ffEAEAEA"), 2, orientation);
    }

    public ColorDividerItemDecoration(@ColorInt int color, int width, @Orientation int orientation) {
        this(color, width, orientation, false);
    }

    public ColorDividerItemDecoration(@ColorInt int color, int width, @Orientation int orientation, boolean over) {
        this.mColor = color;
        this.mWidth = width;
        this.mOrientation = orientation;
        this.mOver = over;
    }

    public void setOver(boolean over) {
        this.mOver = over;
    }

    public void setColor(int color) {
        this.mColor = mColor;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void setShowLast(boolean showLast) {
        this.showLast = showLast;
    }

    /**
     * 绘制图层在itemView以下,如果绘制区域与itemView区域相重叠,会被遮挡
     *
     * @param canvas
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (!mOver) drawDiver(canvas, parent, state);
    }

    /**
     * 绘制在图层的最上层
     *
     * @param canvas
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOver) drawDiver(canvas, parent, state);
    }

    /**
     * 通过outRect设置itemView的偏移长度
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mWidth);
        } else {
            outRect.set(0, 0, mWidth, 0);
        }
    }

    private void drawDiver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(canvas, parent);
        } else {
            drawHorizontal(canvas, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = showLast ? parent.getChildCount() : parent.getChildCount() - 1;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mWidth;
            ColorDrawable drawable = new ColorDrawable(mColor);
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }
        canvas.restore();
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = showLast ? parent.getChildCount() : parent.getChildCount() - 1;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mWidth;
            ColorDrawable drawable = new ColorDrawable(mColor);
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }
        canvas.restore();
    }
}
