package de.linzn.jSocket.core;

import java.util.UUID;

public interface DataInputListener {

    String channel();

    void onEvent(UUID uuid, byte[] bytes);

}
