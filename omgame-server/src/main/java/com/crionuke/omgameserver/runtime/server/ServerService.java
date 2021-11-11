package com.crionuke.omgameserver.runtime.server;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.runtime.events.*;
import com.crionuke.omgameserver.websocket.events.WebSocketMessageReceivedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionClosedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionFailedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionOpenedEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class ServerService {
    static final Logger LOG = Logger.getLogger(ServerService.class);

    final EventBus eventBus;
    final WebSocketClientTable clientTable;

    ServerService(EventBus eventBus) {
        this.eventBus = eventBus;
        clientTable = new WebSocketClientTable();
    }

    @ConsumeEvent(value = WebSocketSessionOpenedEvent.TOPIC)
    void handleWebSocketSessionOpenedEvent(final WebSocketSessionOpenedEvent event) {
        Session session = event.getSession();
        Address address = event.getAddress();
        WebSocketClient webSocketClient = new WebSocketClient(session, address);
        clientTable.put(webSocketClient);
        eventBus.publish(ClientConnectedEvent.TOPIC,
                new ClientConnectedEvent(address, webSocketClient.getId()));
        LOG.infof("Client connected, clientId=%s, sessionId=%s",
                webSocketClient.getId(), session.getId());
    }

    @ConsumeEvent(value = WebSocketMessageReceivedEvent.TOPIC)
    void handleWebSocketMessageReceivedEvent(final WebSocketMessageReceivedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            Address address = event.getAddress();
            String message = event.getMessage();
            eventBus.publish(ServerReceivedMessageEvent.TOPIC,
                    new ServerReceivedMessageEvent(address, client.getId(), message));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Message received, clientId=%s, message=%s", client.getId(), message);
            }
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    @ConsumeEvent(value = WebSocketSessionFailedEvent.TOPIC)
    void handleWebSocketSessionFailedEvent(final WebSocketSessionFailedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client disconnected as session failed, clientId=%d, address=%s",
                    client.getId(), address);
            eventBus.publish(ClientDisconnectedEvent.TOPIC,
                    new ClientDisconnectedEvent(address, client.getId()));
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    @ConsumeEvent(value = WebSocketSessionClosedEvent.TOPIC)
    void handleWebSocketSessionClosedEvent(final WebSocketSessionClosedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client disconnected as session closed, clientId=%d, address=%s",
                    client.getId(), address);
            eventBus.publish(ClientDisconnectedEvent.TOPIC,
                    new ClientDisconnectedEvent(address, client.getId()));
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    @ConsumeEvent(value = DecodeMessageFailedEvent.TOPIC)
    void handleDecodeMessageFailedEvent(final DecodeMessageFailedEvent event) {
        long clientId = event.getClientId();
        if (clientTable.contain(clientId)) {
            WebSocketClient client = clientTable.get(clientId);
            try {
                client.getSession().close();
                Address address = client.getAddress();
                LOG.infof("Session closed as decode message failed, clientId=%d, address=%s",
                        client.getId(), address);
            } catch (IOException e) {
                LOG.warnf("Close session failed, clientId=%d, %s", clientId, e.getMessage());
            }
        } else {
            LOG.warnf("Client not found, clientId=%d", clientId);
        }
    }

    @ConsumeEvent(value = UnicastMessageEncodedEvent.TOPIC)
    void handleUnicastMessageEncodedEvent(final UnicastMessageEncodedEvent event) {
        long clientId = event.getClientId();
        if (clientTable.contain(clientId)) {
            WebSocketClient client = clientTable.get(clientId);
            Session session = client.getSession();
            String message = event.getMessage();
            session.getAsyncRemote().sendText(message);
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Unicast message sent, clientId=%s, message=%s", clientId, message);
            }
        } else {
            LOG.warnf("Client not found, clientId=%d", clientId);
        }
    }

    @ConsumeEvent(value = BroadcastMessageEncodedEvent.TOPIC)
    void handleBroadcastMessageEncodedEvent(final BroadcastMessageEncodedEvent event) {
        String message = event.getMessage();
        int count = 0;
        for (WebSocketClient client : clientTable.get()) {
            Session session = client.getSession();
            session.getAsyncRemote().sendText(message);
            count++;
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Broadcast message sent to %d clients, message=%s", count, message);
        }
    }
}
