package de.linzn.jSocket.test;

import de.linzn.jSocket.core.DataInputListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class TestEventDataClient implements DataInputListener {
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
            System.out.println("ServerSecretPW: " + secretPW + " ServerSecretInt: " + secretInt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
