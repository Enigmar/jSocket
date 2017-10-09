package de.linzn.jSocket.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class JServerConnection implements Runnable {
    private Socket socket;
    private JServer jServer;

    public JServerConnection(Socket socket, JServer jServer) {
        this.socket = socket;
        this.jServer = jServer;
        System.out.println("Create JServerConnection");
    }

    public void setEnable() {
        new ThreadPoolExecutor(1, 1, 250L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())
                .submit(this);
    }

    public void setDisable() {
        this.closeConnection();
    }

    @Override
    public void run() {
        try {
            while (!this.jServer.server.isClosed() && this.isValidConnection()) {
                    /* Read remote stream!*/
                readInput();
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
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }

        }
    }

}
