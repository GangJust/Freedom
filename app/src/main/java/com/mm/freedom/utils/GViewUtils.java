package com.mm.freedom.utils;

import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GViewUtils {

    /**
     * 遍历ViewGroup 获取所有子视图, 该方法会遍历xml节点树, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @return
     */
    public static List<View> deepViewGroup(ViewGroup viewGroup) {
        List<View> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list);
        return list;
    }

    public static void _recursionViewGroup(ViewGroup viewGroup, List<View> list) {
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                _recursionViewGroup((ViewGroup) childAt, list);
            } else {
                list.add(childAt);
            }
        }
    }

    /**
     * 遍历ViewGroup, 获取指定类型的View, 该方法会遍历xml节点树, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @param targetType
     * @param <T>
     * @return
     */
    public static <T extends View> List<T> findViews(ViewGroup viewGroup, Class<T> targetType) {
        List<T> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list, targetType);
        return list;
    }

    public static <T extends View> void _recursionViewGroup(ViewGroup viewGroup, List<T> list, Class<T> targetType) {
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                //GLogUtil.xLog("内部递归ViewGroup: content=" + childAt.getContentDescription() + ", id=" + childAt.getId() + ", name=" + childAt.getClass().getName());
                _recursionViewGroup((ViewGroup) childAt, list, targetType);
            } else {
                //GLogUtil.xLog("内部递归View: content=" + childAt.getContentDescription() + ", id=" + childAt.getId() + ", name=" + childAt.getClass().getName());
                if (targetType.isInstance(childAt)) {
                    list.add(targetType.cast(childAt));
                }
            }
        }
    }

    /**
     * 遍历ViewGroup, 获取指定类型的View, 并且当contains(contentDescription)文本时, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @param targetType
     * @param containsContentDescription
     * @param <T>
     * @return
     */
    public static <T extends View> List<T> findViews(ViewGroup viewGroup, Class<T> targetType, String containsContentDescription) {
        List<T> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list, targetType, containsContentDescription);
        return list;
    }

    public static <T extends View> void _recursionViewGroup(ViewGroup viewGroup, List<T> list, Class<T> targetType, String containsContentDescription) {
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                //GLogUtil.xLog("内部递归ViewGroup: content=" + childAt.getContentDescription() + ", id=" + childAt.getId() + ", name=" + childAt.getClass().getName());
                _recursionViewGroup((ViewGroup) childAt, list, targetType);
            } else {
                //GLogUtil.xLog("内部递归: content=" + childAt.getContentDescription() + ", id=" + childAt.getId() + ", name=" + childAt.getClass().getName());
                if (targetType.isInstance(childAt)) {
                    if (containsContentDescription != null && childAt.getContentDescription().toString().contains(containsContentDescription)) {
                        list.add(targetType.cast(childAt));
                    }
                }
            }
        }
    }

    // 反射获取事件监听器 View$ListenerInfo 内部类
    private static <T extends View> Object getListenerInfo(T view) {
        try {
            Field mListenerInfoField = findFieldRecursiveImpl(view.getClass(), "mListenerInfo");
            mListenerInfoField.setAccessible(true);
            return mListenerInfoField.get(view);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取点击事件, 如果有, 否则返回NULL
     *
     * @param view 需要获取点击事件的视图
     * @return 返回该点击事件的具体实现, 需要响应点击, 请手动调用 onClick 方法.
     */
    public static <T extends View> View.OnClickListener getOnClickListener(T view) {
        Object listenerInfo = getListenerInfo(view);
        if (listenerInfo == null) return null;
        try {
            Field mOnClickListenerField = listenerInfo.getClass().getDeclaredField("mOnClickListener");
            mOnClickListenerField.setAccessible(true);
            Object mOnClickListener = mOnClickListenerField.get(listenerInfo);
            if (mOnClickListener instanceof View.OnClickListener) {
                return (View.OnClickListener) mOnClickListener;
            }
            return null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取点击事件, 如果有, 否则返回NULL
     *
     * @param view 需要获取点击事件的视图
     * @return 返回该点击事件的具体实现, 需要响应长按, 请手动调用 onLongClick 方法.
     */
    public static <T extends View> View.OnLongClickListener getOnLongClickListener(T view) {
        Object listenerInfo = getListenerInfo(view);
        if (listenerInfo == null) return null;
        try {
            Field mOnLongClickListenerField = listenerInfo.getClass().getDeclaredField("mOnLongClickListener");
            mOnLongClickListenerField.setAccessible(true);
            Object mOnLongClickListener = mOnLongClickListenerField.get(listenerInfo);
            if (mOnLongClickListener instanceof View.OnLongClickListener) {
                return (View.OnLongClickListener) mOnLongClickListener;
            }
            return null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 参照: XposedHelpers#findFieldRecursiveImpl
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    private static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }
}
