package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Client;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class ClientRemovedEvent extends AddressedEvent {

    final Client client;

    public ClientRemovedEvent(Client client, Address address) {
        super(address);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "ClientRemovedEvent{" +
                "address=" + address +
                ", client=" + client +
                '}';
    }
}
