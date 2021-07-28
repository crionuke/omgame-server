package com.crionuke.omgameserver.server;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Client;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.MessageReceivedEvent;
import com.crionuke.omgameserver.websocket.WebSocketDispatcher;
import com.crionuke.omgameserver.websocket.events.*;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

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
    final Map<Session, Client> webSocketClients;

    ServerService(WebSocketDispatcher webSocketDispatcher, RuntimeDispatcher runtimeDispatcher) {
        super(ServerService.class.getSimpleName());
        this.webSocketDispatcher = webSocketDispatcher;
        this.runtimeDispatcher = runtimeDispatcher;
        webSocketClients = new HashMap<>();
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<WebSocketEvent> events = webSocketDispatcher.getMulti()
                .emitOn(getSelfExecutor());
        events.filter(event -> event instanceof WebSocketSessionOpenedEvent)
                .onItem().castTo(WebSocketSessionOpenedEvent.class).log().subscribe().with(event -> handleWebSocketSessionOpenedEvent(event));
        events.filter(event -> event instanceof WebSocketMessageReceivedEvent)
                .onItem().castTo(WebSocketMessageReceivedEvent.class).log().subscribe().with(event -> handleWebSocketMessageReceivedEvent(event));
        events.filter(event -> event instanceof WebSocketSessionFailedEvent)
                .onItem().castTo(WebSocketSessionFailedEvent.class).log().subscribe().with(event -> handleWebSocketSessionFailedEvent(event));
        events.filter(event -> event instanceof WebSocketSessionClosedEvent)
                .onItem().castTo(WebSocketSessionClosedEvent.class).log().subscribe().with(event -> handleWebSocketSessionClosedEvent(event));
    }

    void handleWebSocketSessionOpenedEvent(WebSocketSessionOpenedEvent event) {
        Session session = event.getSession();
        Client client = new Client();
        webSocketClients.put(session, client);
        LOG.infof("WebSocket client created, client=%s", client);
    }

    void handleWebSocketMessageReceivedEvent(WebSocketMessageReceivedEvent event) {
        Session session = event.getSession();
        if (webSocketClients.containsKey(session)) {
            Client client = webSocketClients.get(session);
            Address address = event.getAddress();
            String message = event.getMessage();
            runtimeDispatcher.fire(new MessageReceivedEvent(client, address, message));
        } else {
            LOG.infof("WebSocket client not found, session=%s", session);
        }
    }

    void handleWebSocketSessionFailedEvent(WebSocketSessionFailedEvent event) {
        Session session = event.getSession();
        if (webSocketClients.containsKey(session)) {
            Client client = webSocketClients.remove(session);
            LOG.infof("WebSocket client failed, client=%s", client);
        }
    }

    void handleWebSocketSessionClosedEvent(WebSocketSessionClosedEvent event) {
        Session session = event.getSession();
        if (webSocketClients.containsKey(session)) {
            Client client = webSocketClients.remove(session);
            LOG.infof("WebSocket client closed, client=%s", client);
        }
    }
}
