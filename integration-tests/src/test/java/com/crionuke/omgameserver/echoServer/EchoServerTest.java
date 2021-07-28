package com.crionuke.omgameserver.echoServer;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class EchoServerTest {

    @Inject
    EchoServerClient echoServerClient;

    @Test
    void echoServerTest() throws Exception {
        String testMessage = "helloworld";
        echoServerClient.connect();
        echoServerClient.send(testMessage);
        echoServerClient.close();
    }
}
