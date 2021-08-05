package com.crionuke.omgameserver.websocket.endpoints;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.websocket.WebSocketDispatcher;
import com.crionuke.omgameserver.websocket.events.WebSocketMessageReceivedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionClosedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionFailedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionOpenedEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
@ServerEndpoint("/omgameserver/v1/ws/{tenant}/{game}/{worker}")
class WebSocketEndpointV1 {
    static final Logger LOG = Logger.getLogger(WebSocketEndpointV1.class);

    final WebSocketDispatcher websocketDispatcher;

    WebSocketEndpointV1(WebSocketDispatcher websocketDispatcher) {
        this.websocketDispatcher = websocketDispatcher;
        LOG.infof("Created");
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("tenant") String tenant,
                       @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionOpenedEvent(session, address));
        LOG.infof("WebSocket session opened, sessionId=%s, address=%s", session.getId(), address);
    }

    @OnClose
    public void onClose(Session session, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionClosedEvent(session, address));
        LOG.infof("WebSocket session closed, sessionId=%s, address=%s", session.getId(), address);
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionFailedEvent(session, address));
        LOG.infof("WebSocket session failed, sessionId=%s, address=%s, error=%s",
                session.getId(), address, throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("tenant") String tenant,
                          @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        websocketDispatcher.fire(new WebSocketMessageReceivedEvent(session, message, address));
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Got webSocket message, sessionId=%s, address=%s, message=%s",
                    session.getId(), address, message);
        }
    }
}
