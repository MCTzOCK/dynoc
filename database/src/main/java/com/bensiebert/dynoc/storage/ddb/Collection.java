package com.bensiebert.dynoc.storage.ddb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Collection implements Serializable {

    private HashMap<String, Document> documents = new HashMap<>();
    public String name;

    public Collection(String name) {
        this.name = name;
    }

    public void addDocument(Document doc) {
        documents.put(doc.getID(), doc);
    }

    public Document getDocument(String id) {
        return documents.get(id);
    }

    public void removeDocument(String id) {
        documents.remove(id);
    }

    public ArrayList<Document> getDocuments() {
        return new ArrayList<>(documents.values());
    }

    public void clear() {
        documents.clear();
    }

    public boolean containsDocument(String id) {
        return documents.containsKey(id);
    }

    public boolean containsDocument(Document doc) {
        return documents.containsValue(doc);
    }
}
