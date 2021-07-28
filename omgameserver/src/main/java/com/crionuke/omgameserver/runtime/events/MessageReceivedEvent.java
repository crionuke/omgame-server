package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Client;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class MessageReceivedEvent extends AddressedEvent {

    final Client client;
    final String message;

    public MessageReceivedEvent(Client client, Address address, String message) {
        super(address);
        this.client = client;
        this.message = message;
    }

    public Client getClient() {
        return client;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageReceivedEvent{" +
                "address=" + address +
                ", client=" + client +
                ", message='" + message + '\'' +
                '}';
    }
}
