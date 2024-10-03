package com.bensiebert.dynoc.storage.ddb;

import com.bensiebert.dynoc.storage.DocumentDatabase;

import java.io.Serializable;
import java.util.HashMap;

public class Document implements Serializable {

    public HashMap<String, Class> fields = new HashMap<>();
    public HashMap<String, Object> data = new HashMap<>();

    public Document(Collection col) {
        fields.put("#id", String.class);
        data.put("#id", DocumentDatabase.getNewID(this, col));
        fields.put("#initial_collection", String.class);
        data.put("#initial_collection", col.name);

        fields.put("#created", Long.class);
        data.put("#created", System.currentTimeMillis());

        fields.put("#updated", Long.class);
        data.put("#updated", System.currentTimeMillis());

        fields.put("#version", Integer.class);
        data.put("#version", 1);

        fields.put("#deleted", Boolean.class);
        data.put("#deleted", false);
    }

    public void put(String key, Object value) {
        fields.put(key, value.getClass());
        data.put(key, value);
    }

    public String getID() {
        return (String) data.get("#id");
    }

}
