package de.linzn.jSocket.server;

import de.linzn.jSocket.core.ChannelDataEventPacket;
import de.linzn.jSocket.core.ConnectionListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.*;
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
        System.out.println("[" + Thread.currentThread().getName() + "] " + "Create JServerConnection");
    }

    public synchronized void setEnable() {
        new TaskRunnable().runSingleThreadExecutor(this);
    }

    public synchronized void setDisable() {
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

    public boolean readInput() throws IOException {
        BufferedInputStream bInStream = new BufferedInputStream(this.socket.getInputStream());
        DataInputStream dataInput = new DataInputStream(bInStream);

        String headerChannel = dataInput.readUTF();
        int dataSize = dataInput.readInt();
        byte[] fullData = new byte[dataSize];

        for (int i = 0; i < dataSize; i++) {
            fullData[i] = dataInput.readByte();
        }

    /* Default input read*/
        if (headerChannel == null || headerChannel.isEmpty()) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "No channel in header");
            return false;
        } else {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "Data amount: " + fullData.length);
            this.onDataInput(headerChannel, fullData);
            return true;
        }
    }

    public synchronized void writeOutput(String headerChannel, byte[] bytes) {
        if (this.isValidConnection()) {
            try {
                byte[] fullData = bytes;
                int dataSize = fullData.length;
                BufferedOutputStream bOutSream = new BufferedOutputStream(this.socket.getOutputStream());
                DataOutputStream dataOut = new DataOutputStream(bOutSream);

                dataOut.writeUTF(headerChannel);
                dataOut.writeInt(dataSize);

                for (int i = 0; i < dataSize; i++) {
                    dataOut.writeByte(fullData[i]);
                }
                bOutSream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "The JConnection is closed. No output possible!");
        }
    }

    public synchronized void closeConnection() {
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
            this.onDisconnect();
        }
    }

    private void onConnect() {
        System.out.println("[" + Thread.currentThread().getName() + "] " + "Connected to Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ConnectionListener socketConnectionListener : this.jServer.connectionListeners) {
                socketConnectionListener.onConnectEvent(this.uuid);
            }
        });
    }

    private void onDisconnect() {
        System.out.println("[" + Thread.currentThread().getName() + "] " + "Disconnected from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ConnectionListener socketConnectionListener : this.jServer.connectionListeners) {
                socketConnectionListener.onDisconnectEvent(this.uuid);
            }
        });
    }

    private void onDataInput(String channel, byte[] bytes) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + "IncomingData from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ChannelDataEventPacket dataInputListenerObject : this.jServer.dataInputListener) {
                if (dataInputListenerObject.channel.equalsIgnoreCase(channel)) {
                    dataInputListenerObject.incomingDataListener.onEvent(channel, this.uuid, bytes);
                }
            }
        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

}
