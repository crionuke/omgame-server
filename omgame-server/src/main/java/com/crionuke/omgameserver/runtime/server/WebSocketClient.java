package com.crionuke.omgameserver.runtime.server;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Client;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketClient extends Client {

    final Session session;
    final Address address;

    public WebSocketClient(Session session, Address address) {
        super();
        this.session = session;
        this.address = address;
    }

    public Session getSession() {
        return session;
    }

    public Address getAddress() {
        return address;
    }
}
