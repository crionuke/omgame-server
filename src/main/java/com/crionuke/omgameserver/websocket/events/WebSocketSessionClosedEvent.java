package com.crionuke.omgameserver.websocket.events;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketSessionClosedEvent extends WebSocketEvent {

    public WebSocketSessionClosedEvent(Session session, String tenantId, String gameId, String workerId) {
        super(session, tenantId, gameId, workerId);
    }
}
