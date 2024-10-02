package com.bensiebert.dynoc.auth;

import java.util.HashMap;

public class Users {

    public static HashMap<String, User> users = new HashMap<String, User>();

    public static void addUser(User user) {
        users.put(user.name, user);
    }

    public static User getUser(String name) {
        return users.get(name);
    }

    public static boolean userExists(String name) {
        return users.containsKey(name);
    }

    public static void removeUser(String name) {
        users.remove(name);
    }

    public static boolean checkPassword(String name, String password) {
        if(!userExists(name)) {
            return false;
        }

        return users.get(name).hashedPassword.equals(password);
    }
}
