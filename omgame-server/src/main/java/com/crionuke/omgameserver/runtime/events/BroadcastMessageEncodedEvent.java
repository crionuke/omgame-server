package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class BroadcastMessageEncodedEvent extends Event {

    final String message;

    public BroadcastMessageEncodedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BroadcastMessageEncodedEvent{" +
                "message='" + message + '\'' +
                '}';
    }
}
