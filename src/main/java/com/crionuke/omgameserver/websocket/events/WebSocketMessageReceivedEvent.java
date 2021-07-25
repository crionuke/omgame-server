package com.crionuke.omgameserver.websocket.events;

import javax.websocket.Session;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class WebSocketMessageReceivedEvent extends WebSocketEvent {

    final String message;

    public WebSocketMessageReceivedEvent(Session session, String message,
                                         String tenantId, String gameId, String workerId) {
        super(session, tenantId, gameId, workerId);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
