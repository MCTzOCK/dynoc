package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;

import java.io.IOException;

public class ExitCommand implements Command {

    @Override
    public String getCommand() {
        return "exit";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        try {
            proc.client.close();
            if(proc instanceof ClientProcess) {
                ((ClientProcess) proc).clientThread.interrupt();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getUsage() {
        return "Usage: exit";
    }

    @Override
    public String getDescription() {
        return "Exit the current session.";
    }
}
