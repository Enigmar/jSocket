package de.linzn.jSocket.client;

import de.linzn.jSocket.core.DataInputListener;
import de.linzn.jSocket.core.SocketConnectionListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class JClientConnection implements Runnable {
    private String host;
    private int port;
    private Socket socket;
    private boolean keepAlive;
    private UUID uuid;
    private ArrayList<DataInputListener> dataInputListener;
    private ArrayList<SocketConnectionListener> socketConnectListener;
    private ArrayList<SocketConnectionListener> socketDisconnectListener;

    public JClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.keepAlive = true;
        this.socket = new Socket();
        this.dataInputListener = new ArrayList<>();
        this.socketConnectListener = new ArrayList<>();
        this.socketDisconnectListener = new ArrayList<>();
        this.uuid = new UUID(0L, 0L);
        System.out.println("Create JClientConnection");
    }

    public void setEnable() {
        this.keepAlive = true;
        new TaskRunnable().runSingleThreadExecutor(this);
    }

    public void setDisable() {
        this.keepAlive = false;
        this.closeConnection();
    }

    @Override
    public void run() {
        while (this.keepAlive) {
            try {
                this.socket = new Socket(this.host, this.port);
                this.socket.setTcpNoDelay(true);
                this.onConnect();

                while (this.isValidConnection()) {
                    this.readInput();
                }
            } catch (IOException e2) {
                this.closeConnection();
            }
        }
    }


    public boolean isValidConnection() {
        return this.socket.isConnected() && !this.socket.isClosed();
    }

    public boolean readInput() throws IOException {
        DataInputStream inputStream = new DataInputStream(this.socket.getInputStream());
        String channel = inputStream.readUTF();
        if (channel == null || channel.isEmpty()) {
            return false;
        } else {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.readFully(bytes);
            this.onDataInput(channel, bytes);
            return true;
        }
    }

    public void writeOutput(ByteArrayOutputStream bytes) {
        if (this.isValidConnection()) {
            try {
                OutputStream out = this.socket.getOutputStream();
                out.write(bytes.toByteArray());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("The JConnection is closed. No output possible!");
        }
    }

    public void closeConnection() {
        if (!this.socket.isClosed() && this.socket.getRemoteSocketAddress() != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
            this.onDisconnect();
        }
    }

    private void onConnect() {
        System.out.println("Connected to Socket");
        for (SocketConnectionListener socketConnectionListener : this.socketConnectListener) {
            socketConnectionListener.onEvent(this.uuid);
        }
    }

    private void onDisconnect() {
        System.out.println("Disconnected from Socket");
        for (SocketConnectionListener socketConnectionListener : this.socketDisconnectListener) {
            socketConnectionListener.onEvent(this.uuid);
        }
    }

    private void onDataInput(String channel, byte[] bytes) {
        System.out.println("Datainput from Socket");
        for (DataInputListener dataInputEvent : this.dataInputListener) {
            if (dataInputEvent.channel().equalsIgnoreCase(channel)) {
                dataInputEvent.onEvent(this.uuid, bytes);
            }
        }
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


}
