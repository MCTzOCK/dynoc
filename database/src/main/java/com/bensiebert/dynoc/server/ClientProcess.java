package com.bensiebert.dynoc.server;

import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.commands.Command;
import com.bensiebert.dynoc.commands.CommandException;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.logging.Logger;

import java.io.*;
import java.net.Socket;

public class ClientProcess {

    public Socket client;
    public Thread clientThread;
    public User user;

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
                        String cmd = command.split(" ")[0];
                        String[] arg0 = command.split(" ");
                        String[] args = new String[arg0.length - 1];
                        System.arraycopy(arg0, 1, args, 0, arg0.length - 1);
                        Command c = Commands.commands.get(cmd);

                        if (c == null) {
                            out.println("Invalid command");
                            continue;
                        }

                        try {
                            out.println(c.execute(ClientProcess.this, args));
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
