package com.heiden.core;

import java.util.Map;

/**
 * The <code>NoopSpan</code> represents a span implementation without any actual operation. This span implementation is
 * for {@link }, for keeping the memory and gc cost as low as possible.
 */
public class NoopSpan implements AbstractSpan {
    public NoopSpan() {
    }

    @Override
    public AbstractSpan log(Throwable t) {
        return this;
    }

    @Override
    public AbstractSpan errorOccurred() {
        return this;
    }

    public void finish() {

    }


    @Override
    public AbstractSpan tag(String key, String value) {
        return this;
    }

    @Override
    public AbstractSpan tag(AbstractTag<?> tag, String value) {
        return this;
    }

    @Override
    public boolean isEntry() {
        return false;
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public AbstractSpan log(long timestamp, Map<String, ?> event) {
        return this;
    }

    @Override
    public AbstractSpan setOperationName(String operationName) {
        return this;
    }

    @Override
    public AbstractSpan start() {
        return this;
    }

    @Override
    public int getSpanId() {
        return 0;
    }

    @Override
    public String getOperationName() {
        return "";
    }

    @Override
    public AbstractSpan start(long startTime) {
        return this;
    }

    @Override
    public AbstractSpan setPeer(String remotePeer) {
        return this;
    }



    @Override
    public void skipAnalysis() {
    }

    @Override
    public String getTraceId() {
        return null;
    }

    @Override
    public void setTraceId(String traceId) {

    }
}