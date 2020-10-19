package com.heiden.core;

import com.heiden.logging.api.ILog;
import com.heiden.logging.api.LogManager;
import com.heiden.utils.StringUtil;

import static com.heiden.config.Config.Agent.OPERATION_NAME_THRESHOLD;

public class ContextManager {
    private static final ILog logger = LogManager.getLogger(ContextManager.class);
    private static ThreadLocal<AbstractTracerContext> CONTEXT = new ThreadLocal<AbstractTracerContext>();
    private static ContextManagerExtendService EXTEND_SERVICE;

    private static AbstractTracerContext getOrCreate(String operationName, String traceId) {
        AbstractTracerContext context = CONTEXT.get();
        if (context == null) {
            if (EXTEND_SERVICE == null) {
                    EXTEND_SERVICE = ContextManagerExtendService.getInstance();
            }
            context = EXTEND_SERVICE.createTraceContext(operationName, traceId);
            CONTEXT.set(context);
        }
        return context;
    }


    private static AbstractTracerContext get() {
        return CONTEXT.get();
    }

    /**
     * @return the first global trace id if needEnhance. Otherwise, "N/A".
     */
    public static String getGlobalTraceId() {
        AbstractTracerContext segment = CONTEXT.get();
        if (segment == null) {
            return "N/A";
        } else {
            return segment.getReadablePrimaryTraceId();
        }
    }

    public static AbstractSpan createEntrySpan(String operationName, String traceId) {
        AbstractSpan span;
        AbstractTracerContext context;
        operationName = StringUtil.cut(operationName, OPERATION_NAME_THRESHOLD);

        context = getOrCreate(operationName, traceId);
        span = context.createEntrySpan(operationName);

        return span;
    }

    public static AbstractSpan createLocalSpan(String operationName, String traceId) {
        operationName = StringUtil.cut(operationName, OPERATION_NAME_THRESHOLD);
        AbstractTracerContext context = getOrCreate(operationName,traceId);
        return context.createLocalSpan(operationName);
    }

    public static AbstractSpan createExitSpan(String operationName,String remotePeer,String traceId) {

        operationName = StringUtil.cut(operationName, OPERATION_NAME_THRESHOLD);
        AbstractTracerContext context = getOrCreate(operationName, traceId);
        AbstractSpan span = context.createExitSpan(operationName, remotePeer);
        return span;
    }
    /**
     * If not sure has the active span, use this method, will be cause NPE when has no active span, use
     * ContextManager::isActive method to determine whether there has the active span.
     */
    public static AbstractSpan activeSpan() {
        return get().activeSpan();
    }

    /**
     * Recommend use ContextManager::stopSpan(AbstractSpan span), because in that way, the TracingContext core could
     * verify this span is the active one, in order to avoid stop unexpected span. If the current span is hard to get or
     * only could get by low-performance way, this stop way is still acceptable.
     */
    public static void stopSpan() {
        final AbstractTracerContext context = get();
        stopSpan(context.activeSpan(), context);
    }

    public static void stopSpan(AbstractSpan span) {
        stopSpan(span, get());
    }

    private static void stopSpan(AbstractSpan span, final AbstractTracerContext context) {
        if (context.stopSpan(span)) {
            CONTEXT.remove();
        }
    }

    public static boolean isActive() {
        return get() != null;
    }
}
