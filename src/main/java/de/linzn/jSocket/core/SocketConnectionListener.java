package de.linzn.jSocket.core;

import java.util.UUID;

public interface SocketConnectionListener {

    void onEvent(UUID uuid);
}
