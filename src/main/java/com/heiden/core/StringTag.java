package com.heiden.core;

/**
 * A subclass of {@link AbstractTag}, represent a tag with a {@link String} value.
 * <p>
 */
public class StringTag extends AbstractTag<String> {

    public StringTag(String tagKey) {
        super(tagKey);
    }

    public StringTag(int id, String tagKey) {
        super(id, tagKey, false);
    }

    public StringTag(int id, String tagKey, boolean canOverWrite) {
        super(id, tagKey, canOverWrite);
    }

    @Override
    public void set(AbstractSpan span, String tagValue) {
        span.tag(this, tagValue);
    }
}
