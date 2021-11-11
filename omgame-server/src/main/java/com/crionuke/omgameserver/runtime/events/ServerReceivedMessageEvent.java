package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class ServerReceivedMessageEvent extends AddressedEvent {

    public static final String TOPIC = "ServerReceivedMessageEvent";

    final long clientId;
    final String message;

    public ServerReceivedMessageEvent(Address address, long clientId, String message) {
        super(address);
        this.clientId = clientId;
        this.message = message;
    }

    public Address getAddress() {
        return address;
    }

    public long getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageReceivedEvent{" +
                "address=" + address +
                ", clientId=" + clientId +
                '}';
    }
}
