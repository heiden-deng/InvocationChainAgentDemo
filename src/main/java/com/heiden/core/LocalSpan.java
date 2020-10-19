package com.heiden.core;

/**
 * The <code>LocalSpan</code> represents a normal tracing point, such as a local method.
 */
public class LocalSpan extends AbstractTracingSpan {

    public LocalSpan(int spanId, int parentSpanId, String operationName, TracingContext owner) {
        super(spanId, parentSpanId, operationName, owner);
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
    public AbstractSpan setPeer(String remotePeer) {
        return this;
    }
}
