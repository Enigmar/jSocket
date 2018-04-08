/*
 * Copyright (C) 2018. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 *  this file. If not, please write to: niklas.linz@enigmar.de
 */

package de.linzn.jSocket.test.server;

import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.jSocket.test.TestjSocket;

import java.io.*;
import java.util.UUID;

public class TestEventDataServer implements IncomingDataListener {

    @Override
    public void onEvent(String channel, UUID uuid, byte[] bytes) {
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
