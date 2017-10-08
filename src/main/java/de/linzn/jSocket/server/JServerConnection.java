package de.linzn.jSocket.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class JServerConnection extends Thread {
    private Socket socket;
    private JServer jServer;

    public JServerConnection(Socket socket, JServer jServer) {
        this.socket = socket;
        this.jServer = jServer;
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
