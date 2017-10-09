package de.linzn.jSocket.test;

import de.linzn.jSocket.client.JClientConnection;
import de.linzn.jSocket.server.JServer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TestjSocket {

    public TestjSocket(String[] args) {
        if (args[0].equalsIgnoreCase("server")) {
            this.server();
        } else {
            this.client();
        }

    }

    public static void main(String[] input) {
        new TestjSocket(input);
    }

    private void client() {
        JClientConnection jClientConnection1 = new JClientConnection("localhost", 9090);
        jClientConnection1.setEnable();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            outputStream.writeUTF("Client 1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        jClientConnection1.writeOutput(byteArrayOutputStream);
    }

    private void server() {
        JServer jServer = new JServer("localhost", 9090);
        jServer.openServer();
    }
}
