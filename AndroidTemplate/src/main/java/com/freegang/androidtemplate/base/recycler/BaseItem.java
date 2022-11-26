package com.freegang.androidtemplate.base.recycler;

/**
 * Adapter 视图数据模型基类
 *
 * @author Gang
 */
public class BaseItem {
    protected String key;  //默认KEY, 可以有重复

    public BaseItem(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
