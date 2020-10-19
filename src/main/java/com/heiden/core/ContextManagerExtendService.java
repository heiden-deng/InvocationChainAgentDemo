package com.heiden.core;

public class ContextManagerExtendService {

    private static ContextManagerExtendService instance = new ContextManagerExtendService();
    private ContextManagerExtendService(){}

    public static ContextManagerExtendService getInstance(){
        return instance;
    }

    public AbstractTracerContext createTraceContext(String operationName, String traceId) {
        AbstractTracerContext context;
        context = new TracingContext(operationName, traceId);
        return context;
    }
}
