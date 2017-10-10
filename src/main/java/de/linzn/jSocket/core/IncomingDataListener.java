package de.linzn.jSocket.core;

import java.util.UUID;

public interface IncomingDataListener {

    void onEvent(String channel, UUID clientUUID, byte[] dataInBytes);

}
