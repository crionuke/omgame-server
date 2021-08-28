package com.crionuke.omgameserver.websocket;

import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Dispatcher;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class WebSocketDispatcher extends Dispatcher {
    static final Logger LOG = Logger.getLogger(WebSocketDispatcher.class);

    WebSocketDispatcher(Config config) {
        super(config.websocket().bufferSize(), true);
        LOG.infof("Created, bufferSize=%d", config.websocket().bufferSize());
    }
}
