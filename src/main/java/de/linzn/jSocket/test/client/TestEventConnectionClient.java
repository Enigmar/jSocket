/*
 * Copyright (C) 2018. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 *  this file. If not, please write to: niklas.linz@enigmar.de
 */

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
