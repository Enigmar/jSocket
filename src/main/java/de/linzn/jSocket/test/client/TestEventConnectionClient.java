package de.linzn.jSocket.test.client;

import de.linzn.jSocket.core.ConnectionListener;

import java.util.UUID;

public class TestEventConnectionClient implements ConnectionListener {


    @Override
    public void onConnectEvent(UUID clientUUID) {
        System.out.println("Client is connected: New");
    }

    @Override
    public void onDisconnectEvent(UUID clientUUID) {
        System.out.println("Client is disconnected: old");
    }
}
