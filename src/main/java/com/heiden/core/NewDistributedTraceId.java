package com.heiden.core;

import com.heiden.context.GlobalIdGenerator;

public class NewDistributedTraceId extends DistributedTraceId {
    public NewDistributedTraceId() {
        super(GlobalIdGenerator.generate());
    }
}
