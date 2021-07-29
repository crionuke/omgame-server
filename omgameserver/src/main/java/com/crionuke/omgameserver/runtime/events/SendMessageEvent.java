package com.crionuke.omgameserver.runtime.events;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class SendMessageEvent extends RuntimeEvent {

    final long clientId;
    final String message;

    public SendMessageEvent(long clientId, String message) {
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
        return "SendMessageEvent{" +
                "clientId=" + clientId +
                ", message='" + message + '\'' +
                '}';
    }
}
