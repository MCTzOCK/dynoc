package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;
import com.bensiebert.dynoc.server.http.HttpConnection;

public class HelpCommand implements Command {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if(proc instanceof HttpConnection) {
            return new Response(Response.ResponseType.SUCCESS, "Help is not available in HTTP mode.");
        }
        ClientProcess p = (ClientProcess) proc;
        if(args.length == 0) {
            StringBuilder sb = new StringBuilder();
            for(Command cmd : Commands.commands.values()) {
                sb.append(cmd.getCommand());
                sb.append(" - ");
                sb.append(cmd.getDescription());
                sb.append("\n");
            }
            p.out.print(sb.toString() + "\n");
            return null;
        } else {
            for(Command cmd : Commands.commands.values()) {
                if(cmd.getCommand().equals(args[0])) {
                    p.out.print(cmd.getUsage() + "\n");
                    return null;
                }
            }
            throw new CommandException("Command not found.");
        }
    }

    @Override
    public String getUsage() {
        return "Usage: help [command]";
    }

    @Override
    public String getDescription() {
        return "Get a list of available commands and their manual page.";
    }
}
