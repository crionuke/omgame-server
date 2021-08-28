package com.crionuke.omgameserver.websocket.events;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class WebSocketEvent extends Event {

    final Session session;
    final Address address;

    public WebSocketEvent(Session session, Address address) {
        this.session = session;
        this.address = address;
    }

    public Session getSession() {
        return session;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "WebSocketEvent{" +
                "session=" + session +
                ", address=" + address +
                '}';
    }
}
