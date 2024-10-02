package com.bensiebert.dynoc;

import com.bensiebert.dynoc.auth.Permissions;
import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.auth.Users;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.config.Config;
import com.bensiebert.dynoc.crypto.Crypto;
import com.bensiebert.dynoc.logging.Logger;
import com.bensiebert.dynoc.server.Server;

import java.io.IOException;

public class DynocDB {

    public static void main(String[] args) {
        Logger.info("Starting DynocDB version 1.0.0 by Ben Siebert...");
        Config.loadConfig();

        String[] users = Config.props.getOrDefault("users", "").toString().split(",");

        for (String user : users) {
            String password = Config.props.getOrDefault("users." + user + ".password", "").toString();
            String[] permNames = Config.props.getOrDefault("users." + user + ".permissions", "").toString().split(",");
            Permissions[] permissions = new Permissions[permNames.length];
            for (int i = 0; i < permNames.length; i++) {
                permissions[i] = Permissions.valueOf(permNames[i]);
            }
            Users.addUser(new User(
                    user,
                    permissions,
                    password
            ));
        }

        Commands.registerCommands();
        try {
            Server server = new Server(Integer.parseInt(Config.props.getOrDefault("port", 8000).toString()));
        } catch (IOException e) {
            Logger.error("Error starting server: " + e.getMessage());
            System.exit(1);
        }
    }
}
