package de.linzn.jSocket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class JServer extends Thread {
    private String host;
    private int port;
    private ServerSocket server;
    private ArrayList<JServerConnection> jServerConnections;

    public JServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.jServerConnections = new ArrayList<>();
    }

    public void openServer() {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {

    }


    @Override
    public void run() {
        try {
            Socket socket = this.server.accept();
            socket.setTcpNoDelay(true);
            JServerConnection jServerConnection = new JServerConnection(socket, this);
            this.jServerConnections.add(jServerConnection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
