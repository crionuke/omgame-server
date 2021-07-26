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
@ServerEndpoint("/omgameserver/v1/{tenantId}/games/{gameId}/workers/{workerId}/websocket")
class WebSocketEndpointV1 {
    private static final Logger LOG = Logger.getLogger(WebSocketEndpointV1.class);

    final WebSocketEventStream websocketEventStream;

    WebSocketEndpointV1(WebSocketEventStream websocketEventStream) {
        this.websocketEventStream = websocketEventStream;
        LOG.infof("Created");
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("tenantId") String tenantId,
                       @PathParam("gameId") String gameId,
                       @PathParam("workerId") String workerId) {
        LOG.infof("Session opened, sessionId=%s, tenantId=%s, gameId=%s, workerId=%s",
                session.getId(), tenantId, gameId, workerId);
        websocketEventStream.fire(new WebSocketSessionOpenedEvent(session, new Address(tenantId, gameId, workerId)));
    }

    @OnClose
    public void onClose(Session session, @PathParam("tenantId") String tenantId,
                        @PathParam("gameId") String gameId,
                        @PathParam("workerId") String workerId) {
        LOG.infof("Session closed, sessionId=%s, tenantId=%s, gameId=%s, workerId=%s",
                session.getId(), tenantId, gameId, workerId);
        websocketEventStream.fire(new WebSocketSessionClosedEvent(session, new Address(tenantId, gameId, workerId)));
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("tenantId") String tenantId,
                        @PathParam("gameId") String gameId,
                        @PathParam("workerId") String workerId) {
        LOG.infof("Session failed, sessionId=%s, tenantId=%s, gameId=%s, workerId=%s",
                session.getId(), tenantId, gameId, workerId);
        websocketEventStream.fire(new WebSocketSessionFailedEvent(session, new Address(tenantId, gameId, workerId)));
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("tenantId") String tenantId,
                          @PathParam("gameId") String gameId,
                          @PathParam("workerId") String workerId) {
        LOG.debugf("Message received, sessionId=%s, tenantId=%s, gameId=%s, workerId=%s",
                session.getId(), tenantId, gameId, workerId);
        websocketEventStream.fire(new WebSocketMessageReceivedEvent(session, message,
                new Address(tenantId, gameId, workerId)));
    }
}
