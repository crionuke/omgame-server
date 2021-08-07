package com.crionuke.omgameserver.core;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ClientEndpoint
public class WebSocketClient {

    final long timeoutInMillis;
    final BlockingQueue<String> messages;

    Session session;

    public WebSocketClient() {
        this(1000);
    }

    public WebSocketClient(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
        messages = new LinkedBlockingQueue<>();
    }

    public void connect(String webSocketEndpoint) throws DeploymentException, IOException {
        session = ContainerProvider.getWebSocketContainer()
                .connectToServer(this, URI.create(webSocketEndpoint));
    }

    public void send(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public String receive() throws InterruptedException {
        return messages.poll(timeoutInMillis, TimeUnit.MILLISECONDS);
    }

    public void close() throws IOException {
        session.close();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        messages.offer(message, timeoutInMillis, TimeUnit.MILLISECONDS);
    }
}
