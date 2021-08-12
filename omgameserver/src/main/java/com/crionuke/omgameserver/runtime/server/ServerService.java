package com.crionuke.omgameserver.runtime.server;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.*;
import com.crionuke.omgameserver.websocket.WebSocketDispatcher;
import com.crionuke.omgameserver.websocket.events.WebSocketMessageReceivedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionClosedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionFailedEvent;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionOpenedEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class ServerService extends Handler {
    static final Logger LOG = Logger.getLogger(ServerService.class);

    final WebSocketDispatcher webSocketDispatcher;
    final RuntimeDispatcher runtimeDispatcher;
    final WebSocketClientTable clientTable;

    ServerService(WebSocketDispatcher webSocketDispatcher, RuntimeDispatcher runtimeDispatcher) {
        super(ServerService.class.getSimpleName());
        this.webSocketDispatcher = webSocketDispatcher;
        this.runtimeDispatcher = runtimeDispatcher;
        clientTable = new WebSocketClientTable();
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<Event> webSocketEvents = webSocketDispatcher.getMulti().emitOn(getSelfExecutor());
        subscribe(webSocketEvents, WebSocketSessionOpenedEvent.class, this::handleWebSocketSessionOpenedEvent);
        subscribe(webSocketEvents, WebSocketMessageReceivedEvent.class, this::handleWebSocketMessageReceivedEvent);
        subscribe(webSocketEvents, WebSocketSessionFailedEvent.class, this::handleWebSocketSessionFailedEvent);
        subscribe(webSocketEvents, WebSocketSessionClosedEvent.class, this::handleWebSocketSessionClosedEvent);

        Multi<Event> runtimeEvents = runtimeDispatcher.getMulti().emitOn(getSelfExecutor());
        subscribe(runtimeEvents, DecodeMessageFailedEvent.class, this::handleDecodeMessageFailedEvent);
        subscribe(runtimeEvents, UnicastMessageEncodedEvent.class, this::handleUnicastMessageEncodedEvent);
        subscribe(runtimeEvents, BroadcastMessageEncodedEvent.class, this::handleBroadcastMessageEncodedEvent);
    }

    void handleWebSocketSessionOpenedEvent(WebSocketSessionOpenedEvent event) {
        Session session = event.getSession();
        Address address = event.getAddress();
        WebSocketClient webSocketClient = new WebSocketClient(session, address);
        clientTable.put(webSocketClient);
        runtimeDispatcher.fire(new ClientConnectedEvent(address, webSocketClient.getId()));
        LOG.infof("Client connected, clientId=%s, sessionId=%s", webSocketClient.getId(), session.getId());
    }

    void handleWebSocketMessageReceivedEvent(WebSocketMessageReceivedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            Address address = event.getAddress();
            String message = event.getMessage();
            runtimeDispatcher.fire(new ServerReceivedMessageEvent(address, client.getId(), message));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Message received, clientId=%s, message=%s", client.getId(), message);
            }
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleWebSocketSessionFailedEvent(WebSocketSessionFailedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client disconnected as session failed, clientId=%d, address=%s",
                    client.getId(), address);
            runtimeDispatcher.fire(new ClientDisconnectedEvent(address, client.getId()));
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleWebSocketSessionClosedEvent(WebSocketSessionClosedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client disconnected as session closed, clientId=%d, address=%s",
                    client.getId(), address);
            runtimeDispatcher.fire(new ClientDisconnectedEvent(address, client.getId()));
        } else {
            LOG.warnf("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleDecodeMessageFailedEvent(DecodeMessageFailedEvent event) {
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

    void handleUnicastMessageEncodedEvent(UnicastMessageEncodedEvent event) {
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

    void handleBroadcastMessageEncodedEvent(BroadcastMessageEncodedEvent event) {
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
