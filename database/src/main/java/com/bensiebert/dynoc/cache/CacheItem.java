package com.bensiebert.dynoc.cache;

public class CacheItem {
    private final String value;
    private final long expirationTime;

    public CacheItem(String value, long expirationTime) {
        this.value = value;
        this.expirationTime = expirationTime;
    }

    public String getValue() {
        return value;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "{\"value\": \"" + value + "\", \"expirationTime\": " + expirationTime + "}";
    }
}
