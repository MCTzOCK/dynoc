package com.bensiebert.dynoc.server;

import com.bensiebert.dynoc.commands.Command;
import com.bensiebert.dynoc.commands.CommandException;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.commands.ParsedCommand;
import com.bensiebert.dynoc.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProcess extends Connection {

    public Thread clientThread;
    public BufferedReader in;
    public PrintWriter out;

    public ClientProcess(Socket client) {
        this.client = client;
        Logger.info("Accepted client: " + client.getRemoteSocketAddress().toString());
        this.clientThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    out = new PrintWriter(client.getOutputStream(), true);
                    String command = "";

                    out.print("Welcome to DynocDB! Type 'help' for a list of commands.\n");
                    out.print("Type 'exit' to disconnect.\n");
                    out.flush();

                    while (client.isConnected()) {
                        out.print("dynoc> ");
                        out.flush();
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
                            Response o = c.execute(ClientProcess.this, pc.args);
                            if (o != null) {
                                out.println(o);
                            }
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
