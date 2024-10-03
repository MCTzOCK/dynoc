package com.bensiebert.dynoc.storage;

import com.bensiebert.dynoc.storage.ddb.Collection;
import com.bensiebert.dynoc.storage.ddb.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DocumentDatabase extends Database implements Serializable {

    private HashMap<String, Collection> collections = new HashMap<>();

    public DocumentDatabase(String name) {
        super(DatabaseType.DOCUMENT, name);
    }

    public boolean addCollection(Collection col) {
        if(collections.containsKey(col.name)) {
            return false;
        }
        collections.put(col.name, col);
        return true;
    }

    public Collection getCollection(String name) {
        return collections.get(name);
    }

    public boolean removeCollection(String name) {
        if(collections.containsKey(name)) {
            collections.remove(name);
            return true;
        }
        return false;
    }

    public ArrayList<Collection> getCollections() {
        return new ArrayList<>(collections.values());
    }

    public ArrayList<Document> queryEquals(Collection col, String key, Object value) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && doc.data.get(key).equals(value)) {
                results.add(doc);
            }
        }
        return results;
    }

    public ArrayList<Document> queryNotEquals(Collection col, String key, Object value) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && !doc.data.get(key).equals(value)) {
                results.add(doc);
            }
        }
        return results;
    }

    public ArrayList<Document> queryContains(Collection col, String key, Object value) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && doc.data.get(key) instanceof String) {
                if(((String) doc.data.get(key)).contains((String) value)) {
                    results.add(doc);
                }
            }
        }
        return results;
    }

    public ArrayList<Document> queryStartsWith(Collection col, String key, Object value) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && doc.data.get(key) instanceof String) {
                if(((String) doc.data.get(key)).startsWith((String) value)) {
                    results.add(doc);
                }
            }
        }
        return results;
    }

    public ArrayList<Document> queryEndsWith(Collection col, String key, Object value) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && doc.data.get(key) instanceof String) {
                if(((String) doc.data.get(key)).endsWith((String) value)) {
                    results.add(doc);
                }
            }
        }
        return results;
    }

    public ArrayList<Document> queryMatches(Collection col, String key, String regex) {
        ArrayList<Document> results = new ArrayList<>();
        for(Document doc : col.getDocuments()) {
            if(doc.data.containsKey(key) && doc.data.get(key) instanceof String) {
                if(((String) doc.data.get(key)).matches(regex)) {
                    results.add(doc);
                }
            }
        }
        return results;
    }

    public static String getNewID(Document doc, Collection col) {
        String id = "";
        id += doc.hashCode();
        id += String.valueOf(System.currentTimeMillis()).substring(8);
        id += String.valueOf(Math.random()).substring(2, 6);
        id += col.hashCode();
        id += String.valueOf(Math.random()).substring(2, 6);
        return id;
    }

    public Document getDocument(String id) {
        for(Collection col : collections.values()) {
            Document doc = col.getDocument(id);
            if(doc != null) {
                return doc;
            }
        }
        return null;
    }
}
