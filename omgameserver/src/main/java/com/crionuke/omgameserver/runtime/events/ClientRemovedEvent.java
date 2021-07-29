package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class ClientRemovedEvent extends AddressedEvent {

    final long clientId;

    public ClientRemovedEvent(long clientId, Address address) {
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
