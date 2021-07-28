package com.crionuke.omgameserver.websocket;

import com.crionuke.omgameserver.core.Dispatcher;
import com.crionuke.omgameserver.websocket.events.WebSocketEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class WebSocketDispatcher extends Dispatcher<WebSocketEvent> {
    static final Logger LOG = Logger.getLogger(WebSocketDispatcher.class);

    WebSocketDispatcher(
            @ConfigProperty(name = "omgameserver.websocket.bufferSize", defaultValue = "1024") int bufferSize) {
        super(bufferSize, true);
        LOG.infof("Created, bufferSize=%d", bufferSize);
    }
}
