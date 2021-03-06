package com.crionuke.omgameserver.runtime.json;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class JsonService extends Handler {
    static final Logger LOG = Logger.getLogger(JsonService.class);

    final EventBus eventBus;
    final ObjectMapper objectMapper;

    JsonService(EventBus eventBus, Config config, ObjectMapper objectMapper) {
        super("json", config.runtime().jsonService().poolSize());
        this.eventBus = eventBus;
        this.objectMapper = objectMapper;
    }

    @ConsumeEvent(value = ServerReceivedMessageEvent.TOPIC)
    Uni<Void> handleServerReceivedMessageEvent(final ServerReceivedMessageEvent event) {
        return invoke(() -> {
            Address address = event.getAddress();
            long clientId = event.getClientId();
            String message = event.getMessage();
            try {
                LuaValue luaValue = objectMapper.readValue(message, LuaValue.class);
                eventBus.publish(MessageDecodedEvent.TOPIC,
                        new MessageDecodedEvent(address, clientId, luaValue));
                if (LOG.isTraceEnabled()) {
                    LOG.tracef("LuaValue decoded from json, address=%s, clientId=%d, message=%s",
                            address, clientId, message);
                }
            } catch (IOException e) {
                eventBus.publish(DecodeMessageFailedEvent.TOPIC,
                        new DecodeMessageFailedEvent(clientId));
                LOG.debugf("Decode json failed, clientId=%d", clientId);
            }
        });
    }

    @ConsumeEvent(value = UnicastLuaValueEvent.TOPIC)
    Uni<Void> handleUnicastLuaValueEvent(final UnicastLuaValueEvent event) {
        return invoke(() -> {
            long clientId = event.getClientId();
            LuaValue luaValue = event.getLuaValue();
            try {
                String message = objectMapper.writeValueAsString(luaValue);
                eventBus.publish(UnicastMessageEncodedEvent.TOPIC,
                        new UnicastMessageEncodedEvent(clientId, message));
                if (LOG.isTraceEnabled()) {
                    LOG.tracef("Unicast luaValue encoded to json, clientId=%d, luaValue=%s",
                            clientId, luaValue);
                }
            } catch (IOException e) {
                LOG.debugf("Encode unicast luaValue failed, clientId=%d, %e", clientId, e);
            }
        });
    }

    @ConsumeEvent(value = BroadcastLuaValueEvent.TOPIC)
    Uni<Void> handleBroadcastLuaValueEvent(BroadcastLuaValueEvent event) {
        return invoke(() -> {
            LuaValue luaValue = event.getLuaValue();
            try {
                String message = objectMapper.writeValueAsString(luaValue);
                eventBus.publish(BroadcastMessageEncodedEvent.TOPIC,
                        new BroadcastMessageEncodedEvent(message));
                if (LOG.isTraceEnabled()) {
                    LOG.tracef("Broadcast luaValue encoded to json, luaValue=%s", luaValue);
                }
            } catch (IOException e) {
                LOG.debugf("Encode broadcast luaValue failed, clientId=%d, %e", e);
            }
        });
    }
}
