package de.linzn.jSocket.client;

import de.linzn.jSocket.core.ChannelDataEventPacket;
import de.linzn.jSocket.core.ConnectionListener;
import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.jSocket.core.TaskRunnable;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class JClientConnection implements Runnable {
    private String host;
    private int port;
    private Socket socket;
    private boolean keepAlive;
    private UUID uuid;
    private ArrayList<ChannelDataEventPacket> dataInputListener;
    private ArrayList<ConnectionListener> socketConnectListener;
    private ArrayList<ConnectionListener> socketDisconnectListener;

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

    public synchronized void setEnable() {
        this.keepAlive = true;
        new TaskRunnable().runSingleThreadExecutor(this);
    }

    public synchronized void setDisable() {
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
            System.out.println("No channel in header");
            return false;
        } else {
            System.out.println("Data amount: " + fullData.length);
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
            System.out.println("The JConnection is closed. No output possible!");
        }
    }

    public synchronized void closeConnection() {
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
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ConnectionListener socketConnectionListener : this.socketConnectListener) {
                socketConnectionListener.onEvent(this.uuid);
            }
        });
    }

    private void onDisconnect() {
        System.out.println("Disconnected from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ConnectionListener socketConnectionListener : this.socketDisconnectListener) {
                socketConnectionListener.onEvent(this.uuid);
            }
        });
    }

    private void onDataInput(String channel, byte[] bytes) {
        System.out.println("IncomingData from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (ChannelDataEventPacket dataInputListenerObject : this.dataInputListener) {
                if (dataInputListenerObject.channel.equalsIgnoreCase(channel)) {
                    dataInputListenerObject.incomingDataListener.onEvent(channel, this.uuid, bytes);
                }
            }
        });
    }

    public void registerDataInputListener(String channel, IncomingDataListener dataInputListener) {
        this.dataInputListener.add(new ChannelDataEventPacket(channel, dataInputListener));
    }

    public void registerSocketConnectListener(ConnectionListener socketConnectionListener) {
        this.socketConnectListener.add(socketConnectionListener);
    }

    public void registerSocketDisconnectListener(ConnectionListener socketConnectionListener) {
        this.socketDisconnectListener.add(socketConnectionListener);
    }


}
