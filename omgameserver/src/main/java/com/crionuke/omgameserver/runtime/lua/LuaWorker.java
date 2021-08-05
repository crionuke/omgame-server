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

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker extends Handler {
    static final Logger LOG = Logger.getLogger(LuaWorker.class);

    final Address address;
    final LuaChunk luaChunk;
    final RuntimeDispatcher runtimeDispatcher;

    LuaWorker(Address address, LuaChunk luaChunk, RuntimeDispatcher runtimeDispatcher) {
        super(address.toString());
        this.address = address;
        this.luaChunk = luaChunk;
        this.runtimeDispatcher = runtimeDispatcher;
        LOG.infof("Created, address=%s", address);
    }

    void postConstruct() {
        Multi<Event> allEvents = runtimeDispatcher.getMulti()
                .emitOn(getSelfExecutor());

        allEvents.filter(event -> event instanceof TickEvent)
                .onItem().castTo(TickEvent.class)
                .subscribe().with(event -> handleTickEvent(event));

        Multi<AddressedEvent> addressedEvents = allEvents.filter(event -> event instanceof AddressedEvent)
                .onItem().castTo(AddressedEvent.class)
                .filter(event -> event.getAddress().equals(address));

        addressedEvents.filter(event -> event instanceof StartWorkerEvent)
                .onItem().castTo(StartWorkerEvent.class)
                .subscribe().with(event -> handleStartWorkerEvent(event));
        addressedEvents.filter(event -> event instanceof ClientConnectedEvent)
                .onItem().castTo(ClientConnectedEvent.class)
                .subscribe().with(event -> handleClientConnectedEvent(event));
        addressedEvents.filter(event -> event instanceof MessageDecodedEvent)
                .onItem().castTo(MessageDecodedEvent.class)
                .subscribe().with(event -> handleMessageDecodedEvent(event));
        addressedEvents.filter(event -> event instanceof ClientDisconnectedEvent)
                .onItem().castTo(ClientDisconnectedEvent.class)
                .subscribe().with(event -> handleClientDisconnectedEvent(event));
    }

    void handleTickEvent(TickEvent event) {
        long tick = event.getTick();
        LuaTickEvent luaTickEvent = new LuaTickEvent(tick);
        dispatch(luaTickEvent);
    }

    void handleStartWorkerEvent(StartWorkerEvent event) {
        try {
            luaChunk.getChunk().call();
            LOG.infof("Worker started, address=%s", event.getAddress());
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address, luaError.getMessage());
        }
    }

    void handleClientConnectedEvent(ClientConnectedEvent event) {
        long clientId = event.getClientId();
        LuaConnectedEvent luaEvent = new LuaConnectedEvent(clientId);
        dispatch(luaEvent);
        LOG.debugf("Client connected, clientId=%d", clientId);
    }

    void handleMessageDecodedEvent(MessageDecodedEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        LuaReceivedEvent luaEvent = new LuaReceivedEvent(clientId, luaValue);
        dispatch(luaEvent);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Message received, clientId=%d, luaValue=%s", clientId, luaValue);
        }
    }

    void handleClientDisconnectedEvent(ClientDisconnectedEvent event) {
        long clientId = event.getClientId();
        LuaDisconnectedEvent luaEvent = new LuaDisconnectedEvent(clientId);
        dispatch(luaEvent);
        LOG.debugf("Client disconnected, clientId=%d", clientId);
    }

    void dispatch(LuaEvent luaEvent) {
        try {
            luaChunk.getRuntime().dispatch(luaEvent.getId(), luaEvent);
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address, luaError.getMessage());
        }
    }
}
