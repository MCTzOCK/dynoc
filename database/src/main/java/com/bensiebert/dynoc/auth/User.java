package com.bensiebert.dynoc.auth;

public class User {

    public String name;
    public Permission[] permissions;
    public String hashedPassword;

    public User(String name, Permission[] permissions, String hashedPassword) {
        this.name = name;
        this.permissions = permissions;
        this.hashedPassword = hashedPassword;
    }

    public boolean hasPermission(Permission permission) {
        if(permissions == null) {
            return false;
        }
        for(Permission p : permissions) {
            if(p == permission || p == Permission.ALL) {
                return true;
            }
        }
        return false;
    }
}
