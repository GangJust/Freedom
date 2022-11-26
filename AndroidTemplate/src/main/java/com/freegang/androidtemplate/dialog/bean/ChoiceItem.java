package com.freegang.androidtemplate.dialog.bean;


import com.freegang.androidtemplate.base.recycler.BaseItem;

/**
 * 单选项
 */
public class ChoiceItem extends BaseItem {
    private String choiceText;   //选项文本
    private String choiceValue;  //被选中的值

    public ChoiceItem(String choiceText, String choiceValue) {
        this("key_" + System.currentTimeMillis(), choiceText, choiceValue);
    }

    public ChoiceItem(String key, String choiceText, String choiceValue) {
        super(key);
        this.choiceText = choiceText;
        this.choiceValue = choiceValue;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public String getChoiceValue() {
        return choiceValue;
    }

    public void setChoiceValue(String choiceValue) {
        this.choiceValue = choiceValue;
    }

    @Override
    public String toString() {
        return "ChoiceItem{" +
                "choiceText='" + choiceText + '\'' +
                ", choiceValue='" + choiceValue + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
