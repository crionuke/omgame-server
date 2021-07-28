package com.crionuke.omgameserver.websocket;

import com.crionuke.omgameserver.core.Address;
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
@ServerEndpoint("/omgameserver/v1/{tenant}/games/{game}/workers/{worker}/websocket")
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
        LOG.infof("Session opened, sessionId=%s, tenant=%s, game=%s, worker=%s",
                session.getId(), tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionOpenedEvent(session, new Address(tenant, game, worker)));
    }

    @OnClose
    public void onClose(Session session, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        LOG.infof("Session closed, sessionId=%s, tenant=%s, game=%s, worker=%s",
                session.getId(), tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionClosedEvent(session, new Address(tenant, game, worker)));
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        LOG.infof("Session failed, sessionId=%s, tenant=%s, game=%s, worker=%s",
                session.getId(), tenant, game, worker);
        websocketDispatcher.fire(new WebSocketSessionFailedEvent(session, new Address(tenant, game, worker)));
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("tenant") String tenant,
                          @PathParam("game") String game, @PathParam("worker") String worker) {
        LOG.debugf("Message received, message=%s sessionId=%s, tenant=%s, game=%s, worker=%s",
                message, session.getId(), tenant, game, worker);
        websocketDispatcher.fire(new WebSocketMessageReceivedEvent(session, message,
                new Address(tenant, game, worker)));
    }
}
