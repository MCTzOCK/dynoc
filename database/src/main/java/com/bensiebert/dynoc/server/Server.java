package com.bensiebert.dynoc.server;

import com.bensiebert.dynoc.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static Server instance;
    public int port;
    public ServerSocket server;
    public Thread serverThread;

    public Server(int port) throws IOException {
        Server.instance = this;
        this.port = port;
        this.start();
    }

    public void start() throws IOException {
        Logger.info("Attempting to start server on port " + this.port + "...");
        this.server = new ServerSocket(this.port);
        Logger.info("Server started on port " + this.port + "!");

        serverThread = new Thread(){
            public void run(){
                while(true){
                    try {
                        Socket client = server.accept();
                        ClientProcess proc = new ClientProcess(client);
                        proc.clientThread.start();
                    } catch (IOException e) {
                        Logger.error("Error accepting client: " + e.getMessage());
                    }
                }
            }
        };

        serverThread.start();
    }

    public void stop() {
        Logger.info("Stopping server...");
        serverThread.interrupt();
        try {
            server.close();
            Logger.info("Server stopped!");
        } catch (IOException e) {
            Logger.error("Error stopping server: " + e.getMessage());
        }
    }

}
