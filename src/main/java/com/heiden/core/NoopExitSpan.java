package com.heiden.core;

public class NoopExitSpan extends NoopSpan{
    private String peer;

    public NoopExitSpan(String peer) {
        this.peer = peer;
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
