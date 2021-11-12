package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.runtime.events.AddressedEvent;
import com.crionuke.omgameserver.runtime.events.ClientConnectedEvent;
import com.crionuke.omgameserver.runtime.events.ClientDisconnectedEvent;
import com.crionuke.omgameserver.runtime.events.MessageDecodedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaConnectedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaDisconnectedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaReceivedEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker extends AbstractVerticle {
    static final Logger LOG = Logger.getLogger(LuaWorker.class);

    final Address address;
    final LuaChunk luaChunk;
    final int tickEveryMillis;

    LuaWorker(Address address, LuaChunk luaChunk, int tickEveryMillis) {
        this.address = address;
        this.luaChunk = luaChunk;
        this.tickEveryMillis = tickEveryMillis;
        LOG.infof("Created, address=%s, tickEveryMillis=%d", address, tickEveryMillis);
    }

    @Override
    public Uni<Void> asyncStart() {
        return vertx.eventBus().<AddressedEvent>consumer(address.toString())
                .handler(message -> {
                    AddressedEvent event = message.body();
                    if (event instanceof ClientConnectedEvent) {
                        handleClientConnectedEvent((ClientConnectedEvent) event);
                    } else if (event instanceof MessageDecodedEvent) {
                        handleMessageDecodedEvent((MessageDecodedEvent) event);
                    } else if (event instanceof ClientDisconnectedEvent) {
                        handleClientDisconnectedEvent((ClientDisconnectedEvent) event);
                    } else {
                        LOG.warnf("Unknown addressed event, class=%s", event.getClass().getName());
                    }
                })
                .completionHandler();
    }

    void handleClientConnectedEvent(final ClientConnectedEvent event) {
        long clientId = event.getClientId();
        LuaConnectedEvent luaEvent = new LuaConnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client connected, clientId=%d", clientId);
    }

    void handleMessageDecodedEvent(final MessageDecodedEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        LuaReceivedEvent luaEvent = new LuaReceivedEvent(clientId, luaValue);
        luaChunk.fireEvent(luaEvent);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Messasge received, clientId=%d, luaValue=%s", clientId, luaValue);
        }
    }

    void handleClientDisconnectedEvent(final ClientDisconnectedEvent event) {
        long clientId = event.getClientId();
        LuaDisconnectedEvent luaEvent = new LuaDisconnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client disconnected, clientId=%d", clientId);
    }
}
