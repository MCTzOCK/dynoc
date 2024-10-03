package com.bensiebert.dynoc.server;

import com.bensiebert.dynoc.auth.User;

import java.net.Socket;

public abstract class Connection {
    public Socket client;
    public User user;
}
