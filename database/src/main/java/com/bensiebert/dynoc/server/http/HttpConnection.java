package com.bensiebert.dynoc.server.http;

import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.server.Connection;

import java.net.Socket;

public class HttpConnection extends Connection {

    public HttpConnection(Socket client, User user) {
        super();
        this.client = client;
        this.user = user;
    }
}
