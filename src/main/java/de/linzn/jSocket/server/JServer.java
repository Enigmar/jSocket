package de.linzn.jSocket.server;

import de.linzn.jSocket.core.ChannelDataEventPacket;
import de.linzn.jSocket.core.ConnectionListener;
import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JServer implements Runnable {
    public ServerSocket server;
    ArrayList<ChannelDataEventPacket> dataInputListener;
    ArrayList<ConnectionListener> connectionListeners;
    private String host;
    private int port;
    private HashMap<UUID, JServerConnection> jServerConnections;

    public JServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.jServerConnections = new HashMap<>();
        this.dataInputListener = new ArrayList<>();
        this.connectionListeners = new ArrayList<>();
        System.out.println("[" + Thread.currentThread().getName() + "] " + "Create JServer");
    }

    public void openServer() {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
            new TaskRunnable().runSingleThreadExecutor(this);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("jServer");
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

    public void registerIncomingDataListener(String channel, IncomingDataListener dataInputListener) {
        this.dataInputListener.add(new ChannelDataEventPacket(channel, dataInputListener));
    }

    public void registerConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.add(connectionListener);
    }

    public JServerConnection getClient(UUID uuid) {
        return this.jServerConnections.get(uuid);
    }
}
