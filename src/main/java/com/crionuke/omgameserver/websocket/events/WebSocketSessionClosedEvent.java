package com.crionuke.omgameserver.websocket.events;

import com.crionuke.omgameserver.core.Address;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketSessionClosedEvent extends WebSocketEvent {

    public WebSocketSessionClosedEvent(Session session, Address address) {
        super(session, address);
    }
}
