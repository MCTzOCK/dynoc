package com.bensiebert.dynoc.server.objects;

import com.bensiebert.dynoc.server.Response;

public class StringObject implements Response.SerializableObject {

    public String m;

    public StringObject(String m) {
        this.m = m;
    }

    @Override
    public String serialize() {
        return m;
    }
}
