package com.crionuke.omgameserver.echoServer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class EchoServerClient {
    private static final Logger LOG = Logger.getLogger(EchoServerClient.class);

    final URI webSocketUri;
    final long timeoutInMillis;
    final BlockingQueue<String> messages;

    Session session;

    public EchoServerClient(
            @ConfigProperty(name = "integrationTests.echoServer.webSocketEndpoint") String webSocketEndpoint,
            @ConfigProperty(name = "integrationTests.echoServer.timeoutInMillis") long timeoutInMillis) {
        webSocketUri = URI.create(webSocketEndpoint);
        this.timeoutInMillis = timeoutInMillis;
        LOG.infof("Created, uri=%s", webSocketUri);
        messages = new LinkedBlockingQueue<>();
    }

    public void connect() throws DeploymentException, IOException {
        session = ContainerProvider.getWebSocketContainer().connectToServer(EchoServerClient.class, webSocketUri);
        LOG.infof("Connected, session=%s", session);
    }

    public void send(String message) {
        session.getAsyncRemote().sendText(message);
        LOG.infof("Sent, message=%s", message);
    }

    public String receive() throws InterruptedException {
        return messages.poll(timeoutInMillis, TimeUnit.MILLISECONDS);
    }

    public void close() throws IOException {
        session.close();
        LOG.infof("Closed");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        LOG.debugf("Message received, message=%s sessionId=%s", message, session.getId());
        messages.offer(message, timeoutInMillis, TimeUnit.MILLISECONDS);
    }
}
