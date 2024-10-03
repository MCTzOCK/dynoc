package com.bensiebert.dynoc.server;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {

    public String message;
    public ResponseType type;
    public Object data;

    public Response(ResponseType type, String message, Object data) {
        this.type = type;
        this.data = data;
    }

    public Response(ResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Response(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public Response(ResponseType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "{\"type\": \"error\", \"message\": \"Error serializing response.\"}";
        }
    }

    public enum ResponseType {
        SUCCESS,
        ERROR;

        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
