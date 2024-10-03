package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.logging.Logger;
import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;

public class TestCommand implements Command {

    @Override
    public String getCommand() {
        return "test";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if (args.length != 0) {
            String ar = "";
            for (String arg : args) {
                ar += arg + " ";
            }
            Logger.info(ar);
            throw new CommandException("Invalid number of arguments");
        }
        return new Response(Response.ResponseType.SUCCESS, "Test command executed");
    }
}
