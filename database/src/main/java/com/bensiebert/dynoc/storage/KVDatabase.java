package com.bensiebert.dynoc.storage;

import java.io.Serializable;
import java.util.HashMap;

public class KVDatabase extends Database implements Serializable {

    private HashMap<String, Object> data = new HashMap<>();

    public KVDatabase(String name) {
        super(DatabaseType.KEY_VALUE, name);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public void clear() {
        data.clear();
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public HashMap<String, Object> getAll() {
        return data;
    }
}
