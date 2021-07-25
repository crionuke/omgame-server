package com.crionuke.omgameserver.websocket;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import java.net.URI;

@QuarkusTest
public class WebSocketEndpointV1Test extends Assertions {
    private static final Logger LOG = Logger.getLogger(WebSocketEndpointV1Test.class);

    private static final String TEST_TENANT_ID = "crionuke";
    private static final String TEST_GAME_ID = "hostagecrisis";
    private static final String TEST_WORKER_ID = "1234567890";

    @TestHTTPResource("/omgameserver/v1/" + TEST_TENANT_ID +
            "/games/" + TEST_GAME_ID +
            "/workers/" + TEST_WORKER_ID +
            "/websocket")
    URI uri;

    @Test
    public void testEndpoint() throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            assertNotNull(session);
        }
    }

    @ClientEndpoint
    public static class Client {
    }
}
