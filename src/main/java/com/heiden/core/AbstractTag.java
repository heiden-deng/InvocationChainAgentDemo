package com.heiden.core;

public abstract class AbstractTag<T> {

    private int id;

    private boolean canOverwrite;
    /**
     * The key of this Tag.
     */
    protected final String key;

    public AbstractTag(int id, String tagKey, boolean canOverwrite) {
        this.id = id;
        this.key = tagKey;
        this.canOverwrite = canOverwrite;
    }

    public AbstractTag(String key) {
        this(-1, key, false);
    }

    protected abstract void set(AbstractSpan span, T tagValue);

    /**
     * @return the key of this tag.
     */
    public String key() {
        return this.key;
    }

    public boolean sameWith(AbstractTag<T> tag) {
        return canOverwrite && this.id == tag.id;
    }

    public int getId() {
        return id;
    }

    public boolean isCanOverwrite() {
        return canOverwrite;
    }
}
