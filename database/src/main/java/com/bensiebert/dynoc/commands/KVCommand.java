package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;
import com.bensiebert.dynoc.storage.DBManager;
import com.bensiebert.dynoc.storage.Database;
import com.bensiebert.dynoc.storage.KVDatabase;

public class KVCommand implements Command {
    @Override
    public String getCommand() {
        return "kv-db";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if(proc.user == null) {
            throw new CommandException("You must be logged in to use this command.");
        }
        if(args.length < 2) {
            throw new CommandException("Invalid number of arguments. Usage: kv-db <db>");
        }

        Database db = DBManager.getDatabase(args[0]);
        if(db == null) {
            throw new CommandException("Database not found.");
        }
        if(!(db instanceof KVDatabase)) {
            throw new CommandException("Database is not a key-value database.");
        }

        KVDatabase kvdb = (KVDatabase) db;

        String subCommand = args[1];

        switch(subCommand) {
            case "get":
                if(!proc.user.hasPermission(Permission.READ)) {
                    return new Response(Response.ResponseType.ERROR, "You do not have permission to read from this database.");
                }
                String key = args[2];
                Object value = kvdb.get(key);
                if(value == null) {
                    return new Response(Response.ResponseType.ERROR, "Key not found.");
                }
                return new Response(Response.ResponseType.SUCCESS, value);
            case "set":
                if(!proc.user.hasPermission(Permission.WRITE)) {
                    return new Response(Response.ResponseType.ERROR, "You do not have permission to write to this database.");
                }
                key = args[2];
                String val = args[3];
                kvdb.put(key, val);
                return new Response(Response.ResponseType.SUCCESS, "Key set.");
            case "del":
                if(!proc.user.hasPermission(Permission.WRITE)) {
                    return new Response(Response.ResponseType.ERROR, "You do not have permission to write to this database.");
                }
                key = args[2];
                kvdb.remove(key);
                return new Response(Response.ResponseType.SUCCESS, "Key deleted.");
            case "get-all":
                if(!proc.user.hasPermission(Permission.READ)) {
                    return new Response(Response.ResponseType.ERROR, "You do not have permission to read from this database.");
                }
                return new Response(Response.ResponseType.SUCCESS, kvdb.getAll());
        }

        return null;
    }

    @Override
    public String getUsage() {
        String r = "Usage: kv-db <db>\n";
        r += "\t get <key>\n";
        r += "\t set <key> <value>\n";
        r += "\t del <key>\n";
        r += "\t get-all\n";
        return r;
    }

    @Override
    public String getDescription() {
        return "Manage a key-value database.";
    }
}
