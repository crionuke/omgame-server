package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.*;
import com.crionuke.omgameserver.runtime.lua.events.*;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.time.Duration;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker extends Handler {
    static final Logger LOG = Logger.getLogger(LuaWorker.class);

    final Address address;
    final LuaChunk luaChunk;
    final RuntimeDispatcher runtimeDispatcher;
    final int tickEveryMillis;

    LuaWorker(Address address, LuaChunk luaChunk, RuntimeDispatcher runtimeDispatcher, int tickEveryMillis) {
        super(address.toString());
        this.address = address;
        this.luaChunk = luaChunk;
        this.runtimeDispatcher = runtimeDispatcher;
        this.tickEveryMillis = tickEveryMillis;
        LOG.infof("Created, address=%s, tickEveryMillis=%d", address, tickEveryMillis);
    }

    void postConstruct() {
        Multi<Event> allEvents = Multi.createBy().merging()
                // Mix with ticks
                .streams(getTicks(), runtimeDispatcher.getMulti())
                .emitOn(getSelfExecutor());

        subscribe(allEvents, TickEvent.class, this::handleTickEvent);

        Multi<AddressedEvent> addressedEvents = allEvents.filter(event -> event instanceof AddressedEvent)
                .onItem().castTo(AddressedEvent.class)
                .filter(event -> event.getAddress().equals(address));

        subscribe(addressedEvents, StartWorkerEvent.class, this::handleStartWorkerEvent);
        subscribe(addressedEvents, ClientConnectedEvent.class, this::handleClientConnectedEvent);
        subscribe(addressedEvents, MessageDecodedEvent.class, this::handleMessageDecodedEvent);
        subscribe(addressedEvents, ClientDisconnectedEvent.class, this::handleClientDisconnectedEvent);
    }

    Multi<Event> getTicks() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(tickEveryMillis))
                .onItem().transform(tick -> new TickEvent(tick, System.currentTimeMillis()));
    }

    void handleTickEvent(TickEvent event) {
        long tick = event.getTick();
        long time = event.getTime();
        LuaTickEvent luaEvent = new LuaTickEvent(tick, time);
        luaChunk.fireEvent(luaEvent);
    }

    void handleStartWorkerEvent(StartWorkerEvent event) {
        try {
            luaChunk.call();
            luaChunk.fireEvent(new LuaInitEvent());
        } catch (LuaError luaError) {
            LOG.warnf("Start worker failed, address=%s, reason=%s", address, luaError.getMessage());
        }
    }

    void handleClientConnectedEvent(ClientConnectedEvent event) {
        long clientId = event.getClientId();
        LuaConnectedEvent luaEvent = new LuaConnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client connected, clientId=%d", clientId);
    }

    void handleMessageDecodedEvent(MessageDecodedEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        LuaReceivedEvent luaEvent = new LuaReceivedEvent(clientId, luaValue);
        luaChunk.fireEvent(luaEvent);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Message received, clientId=%d, luaValue=%s", clientId, luaValue);
        }
    }

    void handleClientDisconnectedEvent(ClientDisconnectedEvent event) {
        long clientId = event.getClientId();
        LuaDisconnectedEvent luaEvent = new LuaDisconnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client disconnected, clientId=%d", clientId);
    }
}
