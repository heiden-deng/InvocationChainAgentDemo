package com.heiden.utils;

import com.heiden.core.TraceContext;

public class ThreadLocalUtils {
    private static ThreadLocal<TraceContext> CEB_TRACE_CONTEXT = new ThreadLocal<TraceContext>();
    public TraceContext get(){
        return CEB_TRACE_CONTEXT.get();
    }
    public void set(TraceContext traceContext){
        if (CEB_TRACE_CONTEXT.get() != null){
            CEB_TRACE_CONTEXT.remove();
        }
        CEB_TRACE_CONTEXT.set(traceContext);
    }
    public void remove(){
        if (CEB_TRACE_CONTEXT.get() != null){
            CEB_TRACE_CONTEXT.remove();
        }
    }
    public static ThreadLocalUtils init(){
        return new ThreadLocalUtils();
    }
    public ThreadLocalUtils() {
    }
}
