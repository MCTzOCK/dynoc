package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;
import com.bensiebert.dynoc.storage.DBManager;
import com.bensiebert.dynoc.storage.KVDatabase;

public class DBCommand implements Command {

    @Override
    public String getCommand() {
        return "db";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if(proc.user == null) {
            throw new CommandException("You must be logged in to use this command.");
        }
        if(args.length < 1) {
            throw new CommandException("Usage: db <create|delete|list> [name] [relational|document|kv]");
        }

        String subCommand = args[0];

        switch(subCommand) {
            case "create": {
                if(args.length < 2) {
                    throw new CommandException("Usage: db create <name>");
                }
                if(!proc.user.hasPermission(Permission.CREATE)) {
                    throw new CommandException("You do not have permission to create databases.");
                }
                String name = args[1];
                String type = args.length > 2 ? args[2] : "kv";

                if(DBManager.getDatabase(name) != null) {
                    throw new CommandException("Database already exists.");
                }

                switch(type) {
                    case "relational":
                        throw new CommandException("Relational databases are not supported.");
                    case "document":
                        throw new CommandException("Document databases are not supported.");
                    case "kv":
                        DBManager.addDatabase(new KVDatabase(name));
                        break;
                    default:
                        throw new CommandException("Invalid database type.");
                }

                return new Response(Response.ResponseType.SUCCESS, "Database created.");
            }
            case "list": {
                if(!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to list databases.");
                }
                return new Response(Response.ResponseType.SUCCESS, DBManager.getDatabases());
            }
            case "delete": {
                if(args.length < 2) {
                    throw new CommandException("Usage: db delete <name>");
                }
                if(!proc.user.hasPermission(Permission.DELETE)) {
                    throw new CommandException("You do not have permission to delete databases.");
                }
                String name = args[1];
                DBManager.removeDatabase(name);
                return new Response(Response.ResponseType.SUCCESS, "Database deleted.");
            }
            default:
                throw new CommandException("Invalid subcommand.");
        }
    }

    @Override
    public String getUsage() {
        String r = "Usage: db\n";
        r += "\tdb create <name> [relational|document|kv] (Requires CREATE permission)\n";
        r += "\tdb delete <name> (Requires DELETE permission)\n";
        r += "\tdb list (Requires READ permission)\n";
        return r;
    }

    @Override
    public String getDescription() {
        return "Manages databases.";
    }
}
