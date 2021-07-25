package com.crionuke.omgameserver.websocket;

import com.crionuke.omgameserver.websocket.events.WebSocketEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
public class WebSocketEventStream {
    private static final Logger LOG = Logger.getLogger(WebSocketEventStream.class);

    final BroadcastProcessor<WebSocketEvent> processor;
    final Multi<WebSocketEvent> multi;

    WebSocketEventStream(
            @ConfigProperty(name = "omgameserver.websocket.bufferSize", defaultValue = "1024") int bufferSize) {
        processor = BroadcastProcessor.create();
        multi = processor.onOverflow().buffer(bufferSize).onOverflow().drop();
        LOG.infof("Created, bufferSize=%d", bufferSize);
    }

    public Multi<WebSocketEvent> getMulti() {
        return multi;
    }

    public void fire(WebSocketEvent webSocketEvent) {
        processor.onNext(webSocketEvent);
    }
}
