package com.mm.freedom.config;

import androidx.annotation.NonNull;

public class Config {
    //自定义下载路径
    private boolean customDownloadValue;

    //显示完整复制内容
    private boolean clipDataDetailValue;

    //保存表情包
    private boolean saveEmojiValue;

    public boolean isCustomDownloadValue() {
        return customDownloadValue;
    }

    public void setCustomDownloadValue(boolean customDownloadValue) {
        this.customDownloadValue = customDownloadValue;
    }

    public boolean isClipDataDetailValue() {
        return clipDataDetailValue;
    }

    public void setClipDataDetailValue(boolean clipDataDetailValue) {
        this.clipDataDetailValue = clipDataDetailValue;
    }

    public boolean isSaveEmojiValue() {
        return saveEmojiValue;
    }

    public void setSaveEmojiValue(boolean saveEmojiValue) {
        this.saveEmojiValue = saveEmojiValue;
    }

    @NonNull
    @Override
    public String toString() {
        return "Config{" +
                "customDownloadValue=" + customDownloadValue +
                ", clipDataDetailValue=" + clipDataDetailValue +
                ", saveEmojiValue=" + saveEmojiValue +
                '}';
    }
}
