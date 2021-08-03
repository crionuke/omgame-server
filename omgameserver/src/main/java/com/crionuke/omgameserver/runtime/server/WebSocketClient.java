package com.crionuke.omgameserver.runtime.server;

import com.crionuke.omgameserver.core.Client;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketClient extends Client {

    final Session session;

    public WebSocketClient(Session session) {
        super();
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
