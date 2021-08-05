package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class ClientConnectedEvent extends AddressedEvent {

    final long clientId;

    public ClientConnectedEvent(Address address, long clientId) {
        super(address);
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "ClientCreatedEvent{" +
                "address=" + address +
                ", clientId=" + clientId +
                '}';
    }
}
