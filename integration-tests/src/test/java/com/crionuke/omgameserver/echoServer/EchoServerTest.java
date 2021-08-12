package com.crionuke.omgameserver.echoServer;

import com.crionuke.omgameserver.core.WebSocketClient;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
public class EchoServerTest extends Assertions {

    @ConfigProperty(name = "integrationTests.echoServer.webSocketEndpoint")
    String webSocketEndpoint;
    @ConfigProperty(name = "integrationTests.echoServer.timeoutInMillis")
    long timeoutInMillis;

    @Test
    void echoServerTest() throws Exception {
        WebSocketClient webSocketClient = new WebSocketClient(timeoutInMillis);
        webSocketClient.connect(webSocketEndpoint);
        for (int i = 0; i < 10; i++) {
            String clientMessage = String.format("{\"uuid\":\"%s\"}", UUID.randomUUID().toString());
            webSocketClient.send(clientMessage);
            String serverResponse = webSocketClient.receive();
            assertEquals(clientMessage, serverResponse);
        }
        webSocketClient.close();
    }

    @Test
    void decodeFailedTest() throws Exception {
        WebSocketClient webSocketClient = new WebSocketClient(timeoutInMillis);
        webSocketClient.connect(webSocketEndpoint);
        String wrongJson = "{\"uuid\":";
        webSocketClient.send(wrongJson);
        String serverResponse = webSocketClient.receive();
        assertNull(serverResponse);
    }
}
