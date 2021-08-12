package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class DecodeMessageFailedEvent extends Event {

    final long clientId;

    public DecodeMessageFailedEvent(long clientId) {
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "DecodeMessageFailedEvent{" +
                "clientId=" + clientId +
                '}';
    }
}
