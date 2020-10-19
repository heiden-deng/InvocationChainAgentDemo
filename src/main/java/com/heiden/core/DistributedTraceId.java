package com.heiden.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@EqualsAndHashCode
public abstract class DistributedTraceId {
    private final String id;

    public DistributedTraceId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
