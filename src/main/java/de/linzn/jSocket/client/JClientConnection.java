package de.linzn.jSocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class JClientConnection extends Thread {
    private String host;
    private int port;
    private Socket socket;

    public JClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(this.host, this.port);
            this.socket.setTcpNoDelay(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }


    @Override
    public void run() {
        try {
            while (this.socket.isConnected() && !this.socket.isClosed()) {
                this.readStream();
            }
        } catch (IOException e) {
            this.close();
        }
    }

    public void close() {
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
        }
    }

    private void readStream() throws IOException {
        InputStream input = this.socket.getInputStream();

    }

    public void writeStream() {

    }
}
