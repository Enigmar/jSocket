package de.linzn.jSocket.test;

import de.linzn.jSocket.core.DataInputListener;

import java.io.*;
import java.util.UUID;

public class TestEventDataServer implements DataInputListener {
    @Override
    public String channel() {
        return "test_socket_connection";
    }

    @Override
    public void onEvent(UUID uuid, byte[] bytes) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String secretPW = dataInputStream.readUTF();
            int secretInt = dataInputStream.readInt();
            System.out.println("ClientSecretPW: " + secretPW + " ClientSecretInt: " + secretInt);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF(secretPW);
            dataOutputStream.writeInt(secretInt);
            TestjSocket.testjSocket.jServer.getClient(uuid).writeOutput("test_socket_connection", byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
