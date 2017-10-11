package de.linzn.jSocket.core;

import java.util.UUID;

public interface ConnectionListener {

    void onConnectEvent(UUID clientUUID);

    void onDisconnectEvent(UUID clientUUID);
}
