package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Users;
import com.bensiebert.dynoc.crypto.Crypto;
import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;

public class LoginCommand implements Command {

    @Override
    public String getCommand() {
        return "login";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("Invalid number of arguments. Usage: login <username> <password>");
        }
        String username = args[0];
        String hashedPassword = Crypto.hash(args[1]);
        if (proc.user != null) {
            throw new CommandException("Already logged in as " + proc.user.name);
        }
        if(Users.checkPassword(username, hashedPassword)) {
            proc.user = Users.getUser(username);
            return new Response(Response.ResponseType.SUCCESS, "Logged in as " + username, username);
        }
        return new Response(Response.ResponseType.ERROR, "Invalid username or password");
    }

    @Override
    public String getUsage() {
        String r = "Usage: login <username> <password>";
        return r;
    }

    @Override
    public String getDescription() {
        return "Log in to the server.";
    }
}
