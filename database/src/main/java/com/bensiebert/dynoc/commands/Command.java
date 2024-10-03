package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.server.ClientProcess;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;

public interface Command {

    public String getCommand();

    public Response execute(Connection proc, String[] args) throws CommandException;
}
