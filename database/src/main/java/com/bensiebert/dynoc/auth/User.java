package com.bensiebert.dynoc.auth;

public class User {

    public String name;
    public Permissions[] permissions;
    public String hashedPassword;

    public User(String name, Permissions[] permissions, String hashedPassword) {
        this.name = name;
        this.permissions = permissions;
        this.hashedPassword = hashedPassword;
    }
}
