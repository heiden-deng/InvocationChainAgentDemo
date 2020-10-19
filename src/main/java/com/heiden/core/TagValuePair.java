package com.heiden.core;

public class TagValuePair {
    private AbstractTag key;
    private String value;

    public TagValuePair(AbstractTag tag, String value) {
        this.key = tag;
        this.value = value;
    }

    public AbstractTag getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }



    public boolean sameWith(AbstractTag tag) {
        return key.isCanOverwrite() && key.getId() == tag.getId();
    }

    public void setValue(String value) {
        this.value = value;
    }
}
