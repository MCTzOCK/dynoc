package com.bensiebert.dynoc;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.auth.Users;
import com.bensiebert.dynoc.cache.InMemoryCache;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.config.Config;
import com.bensiebert.dynoc.logging.Logger;
import com.bensiebert.dynoc.server.Server;
import com.bensiebert.dynoc.server.http.HttpServer;

import java.io.IOException;

public class DynocDB {

    public static void main(String[] args) {
        Logger.info("Starting DynocDB version 1.0.0 by Ben Siebert...");
        Config.loadConfig();

        String[] users = Config.props.getOrDefault("users", "").toString().split(",");

        for (String user : users) {
            String password = Config.props.getOrDefault("users." + user + ".password", "").toString();
            String[] permNames = Config.props.getOrDefault("users." + user + ".permissions", "").toString().split(",");
            Permission[] permissions = new Permission[permNames.length];
            for (int i = 0; i < permNames.length; i++) {
                permissions[i] = Permission.valueOf(permNames[i]);
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
            if(Boolean.parseBoolean(Config.props.getOrDefault("http.enabled", true).toString())) {
                HttpServer httpServer = new HttpServer(Integer.parseInt(Config.props.getOrDefault("http.port", 8080).toString()));
            }
        } catch (IOException e) {
            Logger.error("Error starting server: " + e.getMessage());
            System.exit(1);
        }

        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.error("Error in cleanup thread: " + e.getMessage());
                }
                InMemoryCache.cleanup();
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("IMC Cleanup Thread");
        cleanupThread.start();
    }
}
