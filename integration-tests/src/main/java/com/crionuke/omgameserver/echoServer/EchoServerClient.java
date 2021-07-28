package com.crionuke.omgameserver.echoServer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ClientEndpoint
@ApplicationScoped
public class EchoServerClient {
    private static final Logger LOG = Logger.getLogger(EchoServerClient.class);

    final URI webSocketUri;

    Session session;

    public EchoServerClient(
            @ConfigProperty(name = "integrationTests.echoServer.webSocketEndpoint") String webSocketEndpoint) {
        webSocketUri = URI.create(webSocketEndpoint);
        LOG.infof("Created, uri=%s", webSocketUri);
    }

    public void connect() throws DeploymentException, IOException {
        session = ContainerProvider.getWebSocketContainer().connectToServer(EchoServerClient.class, webSocketUri);
        LOG.infof("Connected, session=%s", session);
    }

    public void send(String message) {
        session.getAsyncRemote().sendText(message);
        LOG.infof("Sent, message=%s", message);
    }

    public void close() throws IOException {
        session.close();
        LOG.infof("Closed");
    }
}
