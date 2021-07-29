package com.crionuke.omgameserver.echoServer;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

@QuarkusTest
public class EchoServerTest extends Assertions {

    @Inject
    EchoServerClient echoServerClient;

    @Test
    void echoServerTest() throws Exception {
        echoServerClient.connect();
        for (int i = 0; i < 10; i++) {
            String clientMessage = UUID.randomUUID().toString();
            echoServerClient.send(clientMessage);
            String serverResponse = echoServerClient.receive();
            assertEquals(clientMessage, serverResponse);
        }
        echoServerClient.close();
    }
}
