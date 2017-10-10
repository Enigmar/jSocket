package de.linzn.jSocket.core;

public class ChannelDataEventPacket {
    public IncomingDataListener incomingDataListener;
    public String channel;

    public ChannelDataEventPacket(String channel, IncomingDataListener incomingDataListener) {
        this.channel = channel;
        this.incomingDataListener = incomingDataListener;
    }
}
