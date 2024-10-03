package com.bensiebert.dynoc.cache;

import java.util.HashMap;

public class InMemoryCache {

    public static final HashMap<String, CacheItem> cache = new HashMap<>();

    public static void put(String key, String value, long expirationTime) {
        cache.put(key, new CacheItem(value, expirationTime));
    }

    public static CacheItem get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void clear() {
        cache.clear();
    }

    public static boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public static void cleanup() {
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().getExpirationTime() < currentTime && entry.getValue().getExpirationTime() != -1);
    }


}
