package de.linzn.jSocket.server;

import de.linzn.jSocket.core.DataInputListener;
import de.linzn.jSocket.core.SocketConnectionListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JServer extends Thread {
    public ServerSocket server;
    ArrayList<DataInputListener> dataInputListener;
    ArrayList<SocketConnectionListener> socketConnectListener;
    ArrayList<SocketConnectionListener> socketDisconnectListener;
    private String host;
    private int port;
    private HashMap<UUID, JServerConnection> jServerConnections;

    public JServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.jServerConnections = new HashMap<>();
        this.dataInputListener = new ArrayList<>();
        this.socketConnectListener = new ArrayList<>();
        this.socketDisconnectListener = new ArrayList<>();
        this.setName("jServer");
    }

    public void openServer() {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
            new TaskRunnable().runThreadExecutor(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        try {
            this.server.close();
            for (UUID uuid : this.jServerConnections.keySet()) {
                this.jServerConnections.get(uuid).setDisable();
                this.jServerConnections.remove(uuid);
            }
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        do {
            try {
                Socket socket = this.server.accept();
                socket.setTcpNoDelay(true);
                JServerConnection jServerConnection = new JServerConnection(socket, this);
                jServerConnection.setEnable();
                this.jServerConnections.put(jServerConnection.getUUID(), jServerConnection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!this.server.isClosed());
    }

    public void registerDataInputListener(DataInputListener dataInputListener) {
        this.dataInputListener.add(dataInputListener);
    }

    public void registerSocketConnectListener(SocketConnectionListener socketConnectionListener) {
        this.socketConnectListener.add(socketConnectionListener);
    }

    public void registerSocketDisconnectListener(SocketConnectionListener socketConnectionListener) {
        this.socketDisconnectListener.add(socketConnectionListener);
    }

    public JServerConnection getClient(UUID uuid) {
        return this.jServerConnections.get(uuid);
    }
}
