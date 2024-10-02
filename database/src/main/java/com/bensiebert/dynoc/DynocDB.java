package com.bensiebert.dynoc;

import com.bensiebert.dynoc.auth.Permissions;
import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.auth.Users;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.crypto.Crypto;
import com.bensiebert.dynoc.logging.Logger;
import com.bensiebert.dynoc.server.Server;

import java.io.IOException;

public class DynocDB {

    public static void main(String[] args) {
        Logger.info("Starting DynocDB version 1.0.0 by Ben Siebert...");
        Users.addUser(new User(
                "admin",
                new Permissions[]{
                        Permissions.ALL
                },
                Crypto.hash("password")
        ));
        Commands.registerCommands();
        try {
            Server server = new Server(8000);
        } catch (IOException e) {
            Logger.error("Error starting server: " + e.getMessage());
            System.exit(1);
        }
    }
}
