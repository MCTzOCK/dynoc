package com.bensiebert.dynoc.server;

import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.commands.Command;
import com.bensiebert.dynoc.commands.CommandException;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.commands.ParsedCommand;
import com.bensiebert.dynoc.logging.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientProcess extends Connection {

    public Thread clientThread;

    public ClientProcess(Socket client) {
        this.client = client;
        Logger.info("Accepted client: " + client.getRemoteSocketAddress().toString());
        this.clientThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    String command = "";

                    while (!command.equals("exit")) {
                        command = in.readLine();
                        if (command == null) {
                            continue;
                        }
                        ParsedCommand pc = ParsedCommand.parse(command);
                        Command c = Commands.commands.get(pc.command);

                        if (c == null) {
                            out.println("Invalid command");
                            continue;
                        }

                        try {
                            out.println(c.execute(ClientProcess.this, pc.args));
                        } catch (CommandException e) {
                            out.println(new Response(Response.ResponseType.ERROR, e.getMessage()));
                        }

                    }

                    client.close();
                    this.interrupt();
                } catch (IOException e) {
                    Logger.error("Error processing client: " + e.getMessage());
                }
            }
        };
    }
}
