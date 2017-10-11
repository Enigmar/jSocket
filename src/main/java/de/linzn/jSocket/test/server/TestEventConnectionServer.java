package de.linzn.jSocket.test.server;

import de.linzn.jSocket.core.ConnectionListener;

import java.util.UUID;

public class TestEventConnectionServer implements ConnectionListener {


    @Override
    public void onConnectEvent(UUID clientUUID) {
        System.out.println("New client connected: " + clientUUID);
    }

    @Override
    public void onDisconnectEvent(UUID clientUUID) {
        System.out.println("Old client disconnected: " + clientUUID);
    }
}
