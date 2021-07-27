package com.crionuke.omgameserver.websocket;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.websocket.events.WebSocketSessionOpenedEvent;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@QuarkusTest
public class WebSocketDispatcherTest extends Assertions {
    private static final Logger LOG = Logger.getLogger(WebSocketDispatcherTest.class);

    private static final String TEST_TENANT_ID = "crionuke";
    private static final String TEST_GAME_ID = "hostagecrisis";
    private static final String TEST_WORKER_ID = "1234567890";

    @Test
    void overflowTest() {
        WebSocketDispatcher webSocketDispatcher = new WebSocketDispatcher(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        webSocketDispatcher.getMulti().emitOn(executor).subscribe().with(event -> {
            LOG.infof("Got event, sessionId=%s", event.getSession().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }, failure -> fail());
        for (int i = 0; i < 100; i++) {
            webSocketDispatcher.fire(new WebSocketSessionOpenedEvent(new StubWebSocketSession(),
                    new Address(TEST_TENANT_ID, TEST_GAME_ID, TEST_WORKER_ID)));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    class StubWebSocketSession implements Session {

        private final String id;

        StubWebSocketSession() {
            this.id = UUID.randomUUID().toString();
        }

        @Override
        public WebSocketContainer getContainer() {
            return null;
        }

        @Override
        public void addMessageHandler(MessageHandler handler) throws IllegalStateException {

        }

        @Override
        public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {

        }

        @Override
        public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {

        }

        @Override
        public Set<MessageHandler> getMessageHandlers() {
            return null;
        }

        @Override
        public void removeMessageHandler(MessageHandler handler) {

        }

        @Override
        public String getProtocolVersion() {
            return null;
        }

        @Override
        public String getNegotiatedSubprotocol() {
            return null;
        }

        @Override
        public List<Extension> getNegotiatedExtensions() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public long getMaxIdleTimeout() {
            return 0;
        }

        @Override
        public void setMaxIdleTimeout(long milliseconds) {

        }

        @Override
        public void setMaxBinaryMessageBufferSize(int length) {

        }

        @Override
        public int getMaxBinaryMessageBufferSize() {
            return 0;
        }

        @Override
        public void setMaxTextMessageBufferSize(int length) {

        }

        @Override
        public int getMaxTextMessageBufferSize() {
            return 0;
        }

        @Override
        public RemoteEndpoint.Async getAsyncRemote() {
            return null;
        }

        @Override
        public RemoteEndpoint.Basic getBasicRemote() {
            return null;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void close(CloseReason closeReason) throws IOException {

        }

        @Override
        public URI getRequestURI() {
            return null;
        }

        @Override
        public Map<String, List<String>> getRequestParameterMap() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public Map<String, String> getPathParameters() {
            return null;
        }

        @Override
        public Map<String, Object> getUserProperties() {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public Set<Session> getOpenSessions() {
            return null;
        }
    }
}
