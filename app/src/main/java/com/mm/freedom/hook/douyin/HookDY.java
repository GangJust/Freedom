package com.mm.freedom.hook.douyin;

import com.mm.freedom.xposed.XposedExtendHelper;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

// 抖音Hook
public class HookDY extends XposedExtendHelper {
    public HookDY(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
        new HMainActivity(lpparam);
        new HAbsActivity(lpparam);
        new HEmojiBottomSheetDialog(lpparam);
    }
}
