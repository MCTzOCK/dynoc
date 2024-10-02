package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Response;
import com.bensiebert.dynoc.server.objects.StringObject;

public class StatusCommand implements Command {
    @Override
    public String getCommand() {
        return "status";
    }

    @Override
    public Response execute(ClientProcess proc, String[] args) throws CommandException {
        if(args.length != 0) {
            throw new CommandException("Invalid number of arguments");
        }

        if(proc.user == null) {
            throw new CommandException("Not logged in");
        }

        return new Response(Response.ResponseType.SUCCESS, "Logged in as " + proc.user.name, new StringObject(proc.user.name));
    }
}
