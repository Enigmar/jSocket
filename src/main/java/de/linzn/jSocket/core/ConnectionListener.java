package de.linzn.jSocket.core;

import java.util.UUID;

public interface ConnectionListener {

    void onEvent(UUID clientUUID);
}
