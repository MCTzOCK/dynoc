package com.bensiebert.dynoc.storage;

import java.io.Serializable;

public abstract class Database implements Serializable {

    public DatabaseType type;
    public String name;

    public Database(DatabaseType type, String name) {
        this.type = type;
        this.name = name;
    }
}
