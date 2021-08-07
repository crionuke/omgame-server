package com.crionuke.omgameserver.hubServer;

import com.crionuke.omgameserver.core.WebSocketClient;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class HubServerTest extends Assertions {
    private static final Logger LOG = Logger.getLogger(HubServerTest.class);

    @ConfigProperty(name = "integrationTests.hubServer.webSocketEndpoint")
    String webSocketEndpoint;
    @ConfigProperty(name = "integrationTests.hubServer.timeoutInMillis")
    long timeoutInMillis;

    @Test
    void hubServerTest() throws Exception {
        WebSocketClient superior = new WebSocketClient(timeoutInMillis);
        superior.connect(webSocketEndpoint);
        LOG.infof("Superior connected, %s", superior.receive());
        LOG.infof("Superior ready, %s", superior.receive());

        WebSocketClient subordinate = new WebSocketClient(timeoutInMillis);
        subordinate.connect(webSocketEndpoint);
        LOG.infof("Subordinate connected, %s", subordinate.receive());

        String state1 = subordinate.receive();
        LOG.infof("Subordinate ready, %s", state1);
        String state2 = superior.receive();
        LOG.infof("Superior continue, %s", state2);
        assertEquals(state1, state2);

        superior.send("{\"message\":\"hello\"}");
        subordinate.send("{\"message\":\"world\"}");

        String state3 = superior.receive();
        LOG.infof("Superior state, %s", state3);
        String state4 = subordinate.receive();
        LOG.infof("Subordinate state, %s", state4);
        assertEquals(state3, state4);
    }
}
