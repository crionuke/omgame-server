package com.crionuke.omgameserver.websocket.events;

import com.crionuke.omgameserver.core.Address;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketMessageReceivedEvent extends WebSocketEvent {

    public static final String TOPIC = "WebSocketMessageReceivedEvent";

    final String message;

    public WebSocketMessageReceivedEvent(Session session, String message, Address address) {
        super(session, address);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "WebSocketMessageReceivedEvent{" +
                "session=" + session +
                ", address=" + address +
                ", message='" + message + '\'' +
                '}';
    }
}
