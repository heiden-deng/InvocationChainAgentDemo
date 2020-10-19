package com.heiden.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>LogDataEntity</code> represents a collection of {@link KeyValuePair}, contains several fields of a logging
 * operation.
 */
public class LogDataEntity {
    private long timestamp;
    private List<KeyValuePair> logs;

    private LogDataEntity(long timestamp, List<KeyValuePair> logs) {
        this.timestamp = timestamp;
        this.logs = logs;
    }

    public List<KeyValuePair> getLogs() {
        return logs;
    }

    public static class Builder {
        protected List<KeyValuePair> logs;

        public Builder() {
            logs = new LinkedList<>();
        }

        public Builder add(KeyValuePair... fields) {
            Collections.addAll(logs, fields);
            return this;
        }

        public LogDataEntity build(long timestamp) {
            return new LogDataEntity(timestamp, logs);
        }
    }


}
