package com.crionuke.omgameserver.runtime.json;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

import javax.annotation.PostConstruct;
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

    final RuntimeDispatcher runtimeDispatcher;
    final ObjectMapper objectMapper;

    JsonService(Config config, RuntimeDispatcher runtimeDispatcher, ObjectMapper objectMapper) {
        super(config.runtime().jsonService().poolSize(), JsonService.class.getSimpleName());
        this.runtimeDispatcher = runtimeDispatcher;
        this.objectMapper = objectMapper;
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<Event> events = runtimeDispatcher.getMulti()
                .emitOn(getSelfExecutor());

        events.filter(event -> event instanceof ServerReceivedMessageEvent)
                .onItem().castTo(ServerReceivedMessageEvent.class).subscribe()
                .with(event -> handleServerReceivedMessageEvent(event));

        events.filter(event -> event instanceof UnicastLuaValueEvent)
                .onItem().castTo(UnicastLuaValueEvent.class).subscribe()
                .with(event -> handleUnicastLuaValueEvent(event));
        events.filter(event -> event instanceof BroadcastLuaValueEvent)
                .onItem().castTo(BroadcastLuaValueEvent.class).subscribe()
                .with(event -> handleBroadcastLuaValueEvent(event));
    }

    void handleServerReceivedMessageEvent(ServerReceivedMessageEvent event) {
        Address address = event.getAddress();
        long clientId = event.getClientId();
        String message = event.getMessage();
        try {
            LuaValue luaValue = objectMapper.readValue(message, LuaValue.class);
            runtimeDispatcher.fire(new MessageDecodedEvent(address, clientId, luaValue));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("LuaValue decoded from json, address=%s, clientId=%d, message=%s",
                        address, clientId, message);
            }
        } catch (IOException e) {
            LOG.debugf("Decode json failed, clientId=%d", clientId);
        }
    }

    void handleUnicastLuaValueEvent(UnicastLuaValueEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        try {
            String message = objectMapper.writeValueAsString(luaValue);
            runtimeDispatcher.fire(new UnicastMessageEncodedEvent(clientId, message));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Unicast luaValue encoded to json, clientId=%d, luaValue=%s",
                        clientId, luaValue);
            }
        } catch (IOException e) {
            LOG.debugf("Encode unicast luaValue failed, clientId=%d, %e", clientId, e);
        }
    }

    void handleBroadcastLuaValueEvent(BroadcastLuaValueEvent event) {
        LuaValue luaValue = event.getLuaValue();
        try {
            String message = objectMapper.writeValueAsString(luaValue);
            runtimeDispatcher.fire(new BroadcastMessageEncodedEvent(message));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Broadcast luaValue encoded to json, luaValue=%s", luaValue);
            }
        } catch (IOException e) {
            LOG.debugf("Encode broadcast luaValue failed, clientId=%d, %e", e);
        }
    }
}
