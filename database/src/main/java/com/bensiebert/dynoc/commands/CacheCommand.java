package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.cache.InMemoryCache;
import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Response;

import java.util.ArrayList;

public class CacheCommand implements Command {
    @Override
    public String getCommand() {
        return "cache";
    }

    @Override
    public Response execute(ClientProcess proc, String[] args) throws CommandException {
        if (proc.user == null) {
            throw new CommandException("You must be logged in to use this command.");
        }

        if (args.length < 1) {
            throw new CommandException("Usage: cache <get|set|remove|has|clear> [key] [value] [expiration]");
        }

        String subCommand = args[0];

        switch (subCommand) {
            case "clear":
                if(!proc.user.hasPermission(Permission.DELETE)) {
                    throw new CommandException("You do not have permission to clear the cache.");
                }
                InMemoryCache.clear();
                return new Response(Response.ResponseType.SUCCESS, "Cache cleared.");
            case "get":
                if (args.length < 2) {
                    throw new CommandException("Usage: cache get <key>");
                }
                if(!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from the cache.");
                }
                String key = args[1];
                if (InMemoryCache.containsKey(key)) {
                    return new Response(Response.ResponseType.SUCCESS, "Value attached", InMemoryCache.get(key).getValue());
                } else {
                    return new Response(Response.ResponseType.ERROR, "Key not found in cache.");
                }
            case "set":
                if (args.length < 4) {
                    throw new CommandException("Usage: cache set <key> <value> <expiration>");
                }
                if(!proc.user.hasPermission(Permission.WRITE)) {
                    throw new CommandException("You do not have permission to write to the cache.");
                }
                String setKey = args[1];
                String[] value = new String[args.length - 3];
                System.arraycopy(args, 2, value, 0, args.length - 3);
                String setValue = String.join(" ", value);
                long expiration = Long.parseLong(args[args.length - 1]);
                if(expiration != -1L) {
                    expiration = System.currentTimeMillis() + expiration;
                }
                InMemoryCache.put(setKey, setValue, expiration);
                return new Response(Response.ResponseType.SUCCESS, "Key set in cache.");
            case "remove":
                if (args.length < 2) {
                    throw new CommandException("Usage: cache remove <key>");
                }
                if(!proc.user.hasPermission(Permission.DELETE)) {
                    throw new CommandException("You do not have permission to remove from the cache.");
                }
                String removeKey = args[1];
                InMemoryCache.remove(removeKey);
                return new Response(Response.ResponseType.SUCCESS, "Key removed from cache.");
            case "has":
                if (args.length < 2) {
                    throw new CommandException("Usage: cache has <key>");
                }
                if(!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from the cache.");
                }
                String hasKey = args[1];
                if (InMemoryCache.containsKey(hasKey)) {
                    return new Response(Response.ResponseType.SUCCESS, "Key found in cache.");
                } else {
                    return new Response(Response.ResponseType.ERROR, "Key not found in cache.");
                }
            case "list":
                if(!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from the cache.");
                }
                ArrayList<String> keys = new ArrayList<>(InMemoryCache.cache.keySet());
                return new Response(Response.ResponseType.SUCCESS, keys);
        }

        return new Response(Response.ResponseType.ERROR, "Invalid subcommand.");
    }
}
