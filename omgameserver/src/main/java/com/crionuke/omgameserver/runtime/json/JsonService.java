package com.crionuke.omgameserver.runtime.json;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Config;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.MessageDecodedEvent;
import com.crionuke.omgameserver.runtime.events.MessageEncodedEvent;
import com.crionuke.omgameserver.runtime.events.SendLuaValueEvent;
import com.crionuke.omgameserver.runtime.events.ServerReceivedMessageEvent;
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

        events.filter(event -> event instanceof SendLuaValueEvent)
                .onItem().castTo(SendLuaValueEvent.class).subscribe()
                .with(event -> handleSendLuaValueEvent(event));
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

    void handleSendLuaValueEvent(SendLuaValueEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        try {
            String message = objectMapper.writeValueAsString(luaValue);
            runtimeDispatcher.fire(new MessageEncodedEvent(clientId, message));
            if (LOG.isTraceEnabled()) {
                LOG.tracef("LuaValue encoded to json, clientId=%d, luaValue=%s", clientId, luaValue);
            }
        } catch (IOException e) {
            LOG.debugf("Encode LuaValue failed, clientId=%d, %e", clientId, e);
        }
    }
}
