package de.linzn.jSocket.test;

import de.linzn.jSocket.client.JClientConnection;
import de.linzn.jSocket.server.JServer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TestjSocket {
    public static TestjSocket testjSocket;
    public JServer jServer;
    public JClientConnection jClientConnection1;

    public TestjSocket(String[] args) {
        this.server();
        this.client();
    }

    public static void main(String[] input) {
        testjSocket = new TestjSocket(input);
    }

    private void client() {
        jClientConnection1 = new JClientConnection("localhost", 9090);
        jClientConnection1.registerDataInputListener(new TestEventDataClient());
        jClientConnection1.setEnable();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            outputStream.writeUTF("test_socket_connection");
            outputStream.writeUTF("GRE4gterfe23fw3g54EBFilzujasdEWR");
            outputStream.writeInt(51112);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        jClientConnection1.writeOutput(byteArrayOutputStream);
    }

    private void server() {
        jServer = new JServer("localhost", 9090);
        jServer.registerDataInputListener(new TestEventDataServer());
        jServer.openServer();
    }
}
