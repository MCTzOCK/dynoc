package com.bensiebert.dynoc.storage;

import java.io.File;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DBManager {

    protected static ArrayList<Database> dbs = new ArrayList<>();

    public static void addDatabase(Database db) {
        dbs.add(db);
    }

    public static Database getDatabase(String name) {
        for (Database db : dbs) {
            if (db.name.equals(name)) {
                return db;
            }
        }
        return null;
    }

    public static void removeDatabase(String name) {
        Database db = getDatabase(name);
        if (db != null) {
            dbs.remove(db);
        }
    }

    public static ArrayList<Database> getDatabases() {
        return dbs;
    }

    public static void clear() {
        dbs.clear();
    }

    public static void save(File location) {
        if(!location.exists()) {
            location.mkdirs();
        }

        for (Database db : dbs) {
            File dbFile = new File(location, db.name + ".db");
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new java.io.FileOutputStream(dbFile));
                oos.writeObject(db);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void load(File location) {
        if(!location.exists()) {
            return;
        }

        for (File dbFile : location.listFiles()) {
            if (!dbFile.getName().endsWith(".db")) {
                continue;
            }

            try {
                Database db = (Database) new java.io.ObjectInputStream(new java.io.FileInputStream(dbFile)).readObject();
                addDatabase(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
