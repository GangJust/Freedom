package com.freegang.androidtemplate.dialog.bean;


import com.freegang.androidtemplate.base.recycler.BaseItem;

/**
 * 多选项
 */
public class MultipleChoiceItem extends BaseItem {
    private Boolean selected;  //是否被选中
    private String choiceText;   //选项文本
    private String choiceValue;  //被选中的值

    public MultipleChoiceItem(String choiceText, String choiceValue) {
        this(false, choiceText, choiceValue);
    }

    public MultipleChoiceItem(Boolean selected, String choiceText, String choiceValue) {
        this("key_" + System.currentTimeMillis(), selected, choiceText, choiceValue);
    }

    public MultipleChoiceItem(String key, Boolean selected, String choiceText, String choiceValue) {
        super(key);
        this.selected = selected;
        this.choiceText = choiceText;
        this.choiceValue = choiceValue;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
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
        return "MultipleChoiceItem{" +
                "selected=" + selected +
                ", choiceText='" + choiceText + '\'' +
                ", choiceValue='" + choiceValue + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
