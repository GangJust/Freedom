package com.mm.freedom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.mm.freedom.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GResourcesUtils {

    public static String getResourceEntryName(Context context, int resId) {
        return context.getResources().getResourceEntryName(resId);
    }

    public static String[] getResourceEntryNames(Context context, int resId) {
        Resources res = context.getResources();
        TypedArray entriesTypedArray = res.obtainTypedArray(resId);
        List<String> entriesName = new ArrayList<>();
        TypedValue v1 = new TypedValue();
        for (int i1 = 0; i1 < entriesTypedArray.length(); i1++) {
            entriesTypedArray.getValue(i1, v1);
            String name1 = res.getResourceEntryName(v1.resourceId);
            entriesName.add(name1);
        }
        return entriesName.toArray(new String[0]);
    }

    public static int getAnimId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "anim", context.getPackageName());
    }

    public static int getAnimatorId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "animator", context.getPackageName());
    }

    public static int getAttrId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "attr", context.getPackageName());
    }

    public static int getBoolId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "bool", context.getPackageName());
    }

    public static int getColorId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "color", context.getPackageName());
    }

    public static int getDimenId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "dimen", context.getPackageName());
    }

    public static int getDrawableId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "drawable", context.getPackageName());
    }

    public static int getId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "id", context.getPackageName());
    }

    public static int getIntegerId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "integer", context.getPackageName());
    }

    public static int getInterpolatorId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "interpolator", context.getPackageName());
    }

    public static int getLayoutId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "layout", context.getPackageName());
    }

    public static int getPluralsId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "plurals", context.getPackageName());
    }

    public static int getStringId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "string", context.getPackageName());
    }

    public static int getStyleId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "style", context.getPackageName());
    }

    public static int getStyleableId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "styleable", context.getPackageName());
    }

    public static int getXmlId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "xml", context.getPackageName());
    }

    public static int getMipmapId(Context context, String defType) {
        return context.getResources().getIdentifier(defType, "mipmap", context.getPackageName());
    }

    /**
     * 通过反射来读取int[]类型资源Id
     *
     * @param context
     * @param name
     * @return
     */
    public static int[] getResourceDeclareStyleableIntArray(Context context, String name) {
        try {
            Field[] fields2 = Class.forName(context.getPackageName() + ".R$styleable").getFields();
            for (Field f : fields2) {
                if (f.getName().equals(name)) {
                    int[] ret = (int[]) f.get(null);
                    return ret;
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }
}
