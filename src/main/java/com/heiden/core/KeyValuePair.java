package com.heiden.core;

/**
 * The <code>KeyValuePair</code> represents a object which contains a string key and a string value.
 */
public class KeyValuePair {
    private String key;
    private String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


}
