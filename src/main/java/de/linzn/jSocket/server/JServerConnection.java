package de.linzn.jSocket.server;

import de.linzn.jSocket.core.DataInputListener;
import de.linzn.jSocket.core.SocketConnectionListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class JServerConnection implements Runnable {
    private Socket socket;
    private JServer jServer;
    private UUID uuid;

    public JServerConnection(Socket socket, JServer jServer) {
        this.socket = socket;
        this.jServer = jServer;
        this.uuid = UUID.randomUUID();
        System.out.println("Create JServerConnection");
    }

    public void setEnable() {
        new TaskRunnable().runSingleThreadExecutor(this);
    }

    public void setDisable() {
        this.closeConnection();
    }

    @Override
    public void run() {
        this.onConnect();
        try {
            while (!this.jServer.server.isClosed() && this.isValidConnection()) {
                this.readInput();
            }
        } catch (IOException e2) {
            this.closeConnection();
        }
    }

    public boolean isValidConnection() {
        return this.socket.isConnected() && !this.socket.isClosed();
    }

    private boolean readInput() throws IOException {
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
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
            this.onDisconnect();
        }
    }

    private void onConnect() {
        System.out.println("Connected to Socket");
        for (SocketConnectionListener socketConnectionListener : this.jServer.socketConnectListener) {
            socketConnectionListener.onEvent(this.uuid);
        }
    }

    private void onDisconnect() {
        System.out.println("Disconnected from Socket");
        for (SocketConnectionListener socketConnectionListener : this.jServer.socketDisconnectListener) {
            socketConnectionListener.onEvent(this.uuid);
        }
    }

    private void onDataInput(String channel, byte[] bytes) {
        System.out.println("Datainput from Socket");
        for (DataInputListener dataInputEvent : this.jServer.dataInputListener) {
            if (dataInputEvent.channel().equalsIgnoreCase(channel)) {
                dataInputEvent.onEvent(this.uuid, bytes);
            }
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

}
