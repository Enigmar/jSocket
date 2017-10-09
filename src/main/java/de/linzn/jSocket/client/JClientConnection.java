package de.linzn.jSocket.client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JClientConnection implements Runnable {
    private String host;
    private int port;
    private Socket socket;
    private boolean keepAlive;

    public JClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.keepAlive = true;
        System.out.println("Create JClientConnection");
    }

    public void setEnable() {
        this.keepAlive = true;
        new ThreadPoolExecutor(1, 1, 250L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())
                .submit(this);
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

                while (isValidConnection()) {
                    readInput();
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
            System.out.println("C: " + channel);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.readFully(bytes);
            //Data
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
        }
    }

    public void closeConnection() {
        if (!this.socket.isClosed() && this.socket.getRemoteSocketAddress() != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
        }
    }

}
