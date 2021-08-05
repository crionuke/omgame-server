package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class MessageEncodedEvent extends Event {

    final long clientId;
    final String message;

    public MessageEncodedEvent(long clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }

    public long getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageEncodedEvent{" +
                "clientId=" + clientId +
                '}';
    }
}
