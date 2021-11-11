package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class ClientDisconnectedEvent extends AddressedEvent {

    public static final String TOPIC = "ClientDisconnectedEvent";

    final long clientId;

    public ClientDisconnectedEvent(Address address, long clientId) {
        super(address);
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "ClientRemovedEvent{" +
                "address=" + address +
                ", clientId=" + clientId +
                '}';
    }
}
