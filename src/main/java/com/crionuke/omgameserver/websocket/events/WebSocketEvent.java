package com.crionuke.omgameserver.websocket.events;

import com.crionuke.omgameserver.core.Event;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class WebSocketEvent extends Event {

    final Session session;
    final String tenantId;
    final String gameId;
    final String workerId;

    public WebSocketEvent(Session session, String tenantId, String gameId, String workerId) {
        this.session = session;
        this.tenantId = tenantId;
        this.gameId = gameId;
        this.workerId = workerId;
    }

    public Session getSession() {
        return session;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getWorkerId() {
        return workerId;
    }
}
