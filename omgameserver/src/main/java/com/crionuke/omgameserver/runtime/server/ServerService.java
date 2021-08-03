package com.crionuke.omgameserver.runtime.server;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.ClientCreatedEvent;
import com.crionuke.omgameserver.runtime.events.ClientRemovedEvent;
import com.crionuke.omgameserver.runtime.events.MessageEncodedEvent;
import com.crionuke.omgameserver.runtime.events.ServerReceivedMessageEvent;
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
        Multi<Event> webSocketEvents = webSocketDispatcher.getMulti()
                .emitOn(getSelfExecutor());
        webSocketEvents.filter(event -> event instanceof WebSocketSessionOpenedEvent)
                .onItem().castTo(WebSocketSessionOpenedEvent.class).log().subscribe()
                .with(event -> handleWebSocketSessionOpenedEvent(event));
        webSocketEvents.filter(event -> event instanceof WebSocketMessageReceivedEvent)
                .onItem().castTo(WebSocketMessageReceivedEvent.class).subscribe()
                .with(event -> handleWebSocketMessageReceivedEvent(event));
        webSocketEvents.filter(event -> event instanceof WebSocketSessionFailedEvent)
                .onItem().castTo(WebSocketSessionFailedEvent.class).log().subscribe()
                .with(event -> handleWebSocketSessionFailedEvent(event));
        webSocketEvents.filter(event -> event instanceof WebSocketSessionClosedEvent)
                .onItem().castTo(WebSocketSessionClosedEvent.class).log().subscribe()
                .with(event -> handleWebSocketSessionClosedEvent(event));

        Multi<Event> runtimeEvents = runtimeDispatcher.getMulti()
                .emitOn(getSelfExecutor());
        runtimeEvents.filter(event -> event instanceof MessageEncodedEvent)
                .onItem().castTo(MessageEncodedEvent.class).log().subscribe()
                .with(event -> handleMessageEncodedEvent(event));
    }

    void handleWebSocketSessionOpenedEvent(WebSocketSessionOpenedEvent event) {
        Session session = event.getSession();
        WebSocketClient webSocketClient = new WebSocketClient(session);
        clientTable.put(webSocketClient);
        Address address = event.getAddress();
        runtimeDispatcher.fire(new ClientCreatedEvent(address, webSocketClient.getId()));
        LOG.infof("Client created, sessionId=%s", session.getId());
    }

    void handleWebSocketMessageReceivedEvent(WebSocketMessageReceivedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            Address address = event.getAddress();
            String message = event.getMessage();
            runtimeDispatcher.fire(new ServerReceivedMessageEvent(address, client.getId(), message));
            LOG.tracef("Message received, clientId=%s, message=%s", client.getId(), message);
        } else {
            LOG.infof("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleWebSocketSessionFailedEvent(WebSocketSessionFailedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client removed, clientId=%d, address=%s", client.getId(), address);
            runtimeDispatcher.fire(new ClientRemovedEvent(address, client.getId()));
        } else {
            LOG.infof("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleWebSocketSessionClosedEvent(WebSocketSessionClosedEvent event) {
        Session session = event.getSession();
        if (clientTable.contain(session)) {
            WebSocketClient client = clientTable.get(session);
            clientTable.remove(client);
            Address address = event.getAddress();
            LOG.infof("Client removed, clientId=%d, address=%s", client.getId(), address);
            runtimeDispatcher.fire(new ClientRemovedEvent(address, client.getId()));
        } else {
            LOG.infof("Client not found, sessionId=%s", session.getId());
        }
    }

    void handleMessageEncodedEvent(MessageEncodedEvent event) {
        long clientId = event.getClientId();
        if (clientTable.contain(clientId)) {
            WebSocketClient client = clientTable.get(clientId);
            Session session = client.getSession();
            String message = event.getMessage();
            session.getAsyncRemote().sendText(message);
            LOG.tracef("Message sent, clientId=%s, message=%s", clientId, message);
        } else {
            LOG.infof("Client not found, clientId=%d", clientId);
        }
    }
}
