package com.crionuke.omgameserver.runtime.server;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketClientTable {

    final Set<WebSocketClient> table;
    final Map<Long, WebSocketClient> idIndex;
    final Map<Session, WebSocketClient> sessionIndex;

    public WebSocketClientTable() {
        table = new HashSet<>();
        idIndex = new HashMap<>();
        sessionIndex = new HashMap<>();
    }

    void put(WebSocketClient client) {
        table.add(client);
        idIndex.put(client.getId(), client);
        sessionIndex.put(client.getSession(), client);
    }

    boolean contain(long id) {
        return idIndex.containsKey(id);
    }

    boolean contain(Session session) {
        return sessionIndex.containsKey(session);
    }

    Set<WebSocketClient> get() {
        return table;
    }

    WebSocketClient get(long id) {
        return idIndex.get(id);
    }

    WebSocketClient get(Session session) {
        return sessionIndex.get(session);
    }

    void remove(WebSocketClient client) {
        table.remove(client);
        idIndex.remove(client.getId());
        sessionIndex.remove(client.getSession());
    }
}
