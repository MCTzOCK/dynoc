package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;

public class ShutdownCommand implements Command {

    @Override
    public String getCommand() {
        return "shutdown";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if(proc.user == null) {
            throw new CommandException("You must be logged in to use this command.");
        }
        if(!proc.user.hasPermission(Permission.ALL)) {
            throw new CommandException("You do not have permission to shutdown the server.");
        }
        System.exit(0);
        return null;
    }

    @Override
    public String getUsage() {
        return "Usage: shutdown (Requires ALL permission)";
    }

    @Override
    public String getDescription() {
        return "Shuts down Dynoc.";
    }
}
