package com.bensiebert.dynoc.server.http;

import com.bensiebert.dynoc.auth.User;
import com.bensiebert.dynoc.auth.Users;
import com.bensiebert.dynoc.commands.Command;
import com.bensiebert.dynoc.commands.CommandException;
import com.bensiebert.dynoc.commands.Commands;
import com.bensiebert.dynoc.commands.ParsedCommand;
import com.bensiebert.dynoc.crypto.Crypto;
import com.bensiebert.dynoc.logging.Logger;
import com.bensiebert.dynoc.server.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class HttpServer {

    public static HttpServer instance;
    public ServerSocket server;
    public Thread serverThread;
    public int port;

    public HttpServer(int port) throws IOException {
        HttpServer.instance = this;
        this.port = port;
        this.start();
    }

    public void start() throws IOException {
        Logger.info("Starting HTTP server on port " + this.port + "...");
        this.server = new ServerSocket(this.port);
        Logger.info("HTTP server started on port " + this.port + "!");

        serverThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Socket s = server.accept();
                        handleClient(s);
                    } catch (IOException e) {
                        Logger.error("Error accepting HTTP connection: " + e.getMessage());
                    }
                }
            }
        };

        serverThread.setName("HTTP Server Thread");

        serverThread.start();
    }

    public void handleClient(Socket socket) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                content.append(line).append("\n");
            }

            HashMap<String, String> headers = new HashMap<>();
            String verb = content.toString().split(" ")[0];
            String path = content.toString().split(" ")[1];
            String protocol = content.toString().split(" ")[2];

            for (String line2 : content.toString().split("\n")) {
                if (line2.contains(":")) {
                    headers.put(line2.split(":")[0], line2.split(":")[1].trim());
                }
            }

            if (headers.get("X-Username") == null || headers.get("X-Password") == null) {
                writer.write(getResponse("401 Unauthorized", new Response(Response.ResponseType.ERROR, "Missing X-Username or X-Password").toString()));
                writer.flush();
                return;
            }

            if (!Users.checkPassword(headers.get("X-Username"), Crypto.hash(headers.get("X-Password")))) {
                writer.write(getResponse("401 Unauthorized", new Response(Response.ResponseType.ERROR, "Invalid X-Username or X-Password").toString()));
                writer.flush();
                return;
            }

            User user = Users.getUser(headers.get("X-Username"));

            switch(path) {
                case "/": {
                    writer.write(getResponse("200 OK", new Response(Response.ResponseType.SUCCESS, "Welcome to DynocDB!").toString()));
                    writer.flush();
                    return;
                }
                case "/exec": {
                    String command = headers.get("X-Command");
                    if (command == null) {
                        writer.write(getResponse("400 Bad Request", new Response(Response.ResponseType.ERROR, "Missing X-Command").toString()));
                        writer.flush();
                        return;
                    }
                    HttpConnection con = new HttpConnection(socket, user);

                    ParsedCommand pc = ParsedCommand.parse(command);
                    Command c = Commands.commands.get(pc.command);

                    if (c == null) {
                        writer.write(getResponse("404 Not Found", new Response(Response.ResponseType.ERROR, "Invalid command").toString()));
                        writer.flush();
                        return;
                    }

                    Response res = null;

                    try {
                        res = c.execute(con, pc.args);
                    } catch(CommandException e){
                        res = new Response(Response.ResponseType.ERROR, e.getMessage());
                    }
                    String status = "200 OK";

                    if (res.type == Response.ResponseType.ERROR) {
                        status = "500 Internal Server Error";
                    }

                    writer.write(getResponse(status, res.toString()));
                    writer.flush();

                    return;
                }
                default: {
                    writer.write(getResponse("404 Not Found", new Response(Response.ResponseType.ERROR, "Not Found").toString()));
                    writer.flush();
                    return;
                }
            }
        } catch (Exception e) {
            Logger.error("Error reading HTTP request: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                socket.close();
            } catch (IOException e) {
                Logger.error("Error closing HTTP connection: " + e.getMessage());
            }
        }
    }

    public String getResponse(String status, String content) {
        String res = "HTTP/1.1 " + status + "\n";
        res += "Content-Type: application/json\n";
        res += "server: DynocDB\n";
        res += "date: " + new Date() + "\n";
        res += "Content-Length: " + content.length() + "\n\n";
        res += content;

        return res;
    }
}
