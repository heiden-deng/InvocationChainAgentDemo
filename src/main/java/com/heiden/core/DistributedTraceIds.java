package com.heiden.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DistributedTraceIds {
    private LinkedList<DistributedTraceId> relatedGlobalTraces;

    public DistributedTraceIds() {
        relatedGlobalTraces = new LinkedList<DistributedTraceId>();
    }

    public List<DistributedTraceId> getRelatedGlobalTraces() {
        return Collections.unmodifiableList(relatedGlobalTraces);
    }

    public void append(DistributedTraceId distributedTraceId) {
        if (relatedGlobalTraces.size() > 0 && relatedGlobalTraces.getFirst() instanceof NewDistributedTraceId) {
            relatedGlobalTraces.removeFirst();
        }
        if (!relatedGlobalTraces.contains(distributedTraceId)) {
            relatedGlobalTraces.add(distributedTraceId);
        }
    }
}
