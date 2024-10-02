package com.bensiebert.dynoc.server;

public class Response {

    public String message;
    public ResponseType type;
    public SerializableObject data;

    public Response(ResponseType type, String message, SerializableObject data) {
        this.type = type;
        this.data = data;
    }

    public Response(ResponseType type, SerializableObject data) {
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
        String res = "";
        res += "{\"type\": \"" + type + "\",";
        if(message != null) {
            res += "\"message\": \"" + message + "\",";
        }
        if(data != null) {
            res += "\"data\": \"" + data.serialize() + "\"";
        }
        res += "}";
        return res;
    }

    public enum ResponseType {
        SUCCESS,
        ERROR;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public interface SerializableObject {
        public String serialize();
    }
}
