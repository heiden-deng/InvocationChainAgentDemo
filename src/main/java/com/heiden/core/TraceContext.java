package com.heiden.core;

public class TraceContext {
    private String traceId;
    private String parentId;

    public TraceContext() {
        traceId = null;
        parentId = null;
    }

    public TraceContext(String traceId, String parentId) {
        this.traceId = traceId;
        this.parentId = parentId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
