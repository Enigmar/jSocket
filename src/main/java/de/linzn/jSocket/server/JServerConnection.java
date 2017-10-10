package de.linzn.jSocket.server;

import de.linzn.jSocket.core.DataInputListener;
import de.linzn.jSocket.core.SocketConnectionListener;
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
        System.out.println("Create JServerConnection");
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
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (SocketConnectionListener socketConnectionListener : jServer.socketConnectListener) {
                socketConnectionListener.onEvent(uuid);
            }
        });
    }

    private void onDisconnect() {
        System.out.println("Disconnected from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (SocketConnectionListener socketConnectionListener : this.jServer.socketDisconnectListener) {
                socketConnectionListener.onEvent(this.uuid);
            }
        });
    }

    private void onDataInput(String channel, byte[] bytes) {
        System.out.println("Datainput from Socket");
        new TaskRunnable().runSingleThreadExecutor(() -> {
            for (DataInputListener dataInputEvent : this.jServer.dataInputListener) {
                if (dataInputEvent.channel().equalsIgnoreCase(channel)) {
                    dataInputEvent.onEvent(this.uuid, bytes);
                }
            }
        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

}
