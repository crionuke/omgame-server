package com.crionuke.omgameserver.websocket.endpoints;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.websocket.events.WebSocketMessageReceivedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionClosedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionFailedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionOpenedEvent;
import io.quarkus.runtime.Startup;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
@ServerEndpoint("/omgameserver/v1/ws/{tenant}/{game}/{worker}")
class WebSocketEndpointV1 {
    static final Logger LOG = Logger.getLogger(WebSocketEndpointV1.class);

    final EventBus eventBus;

    WebSocketEndpointV1(EventBus eventBus) {
        this.eventBus = eventBus;
        LOG.infof("Created");
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("tenant") String tenant,
                       @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        eventBus.publish(WebSocketSessionOpenedEvent.TOPIC, new WebSocketSessionOpenedEvent(session, address));
        LOG.infof("WebSocket session opened, sessionId=%s, address=%s", session.getId(), address);
    }

    @OnClose
    public void onClose(Session session, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        eventBus.publish(WebSocketSessionClosedEvent.TOPIC, new WebSocketSessionClosedEvent(session, address));
        LOG.infof("WebSocket session closed, sessionId=%s, address=%s", session.getId(), address);
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("tenant") String tenant,
                        @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        eventBus.publish(WebSocketSessionFailedEvent.TOPIC, new WebSocketSessionFailedEvent(session, address));
        LOG.infof("WebSocket session failed, sessionId=%s, address=%s, error=%s",
                session.getId(), address, throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("tenant") String tenant,
                          @PathParam("game") String game, @PathParam("worker") String worker) {
        Address address = new Address(tenant, game, worker);
        eventBus.publish(WebSocketMessageReceivedEvent.TOPIC,
                new WebSocketMessageReceivedEvent(session, message, address));
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Got webSocket message, sessionId=%s, address=%s, message=%s",
                    session.getId(), address, message);
        }
    }
}
