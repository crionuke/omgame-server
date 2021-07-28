package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.*;
import com.crionuke.omgameserver.runtime.lua.events.*;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;

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
        super(LuaWorker.class.getSimpleName());
        this.address = address;
        this.luaChunk = luaChunk;
        this.runtimeDispatcher = runtimeDispatcher;
        LOG.infof("Created, address=%s", address);
    }

    void postConstruct() {
        Multi<RuntimeEvent> allEvents = runtimeDispatcher.getMulti()
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
        addressedEvents.filter(event -> event instanceof ClientCreatedEvent)
                .onItem().castTo(ClientCreatedEvent.class)
                .subscribe().with(event -> handleClientCreatedEvent(event));
        addressedEvents.filter(event -> event instanceof MessageReceivedEvent)
                .onItem().castTo(MessageReceivedEvent.class)
                .subscribe().with(event -> handleMessageReceivedEvent(event));
        addressedEvents.filter(event -> event instanceof ClientRemovedEvent)
                .onItem().castTo(ClientRemovedEvent.class)
                .subscribe().with(event -> handleClientRemovedEvent(event));
    }

    void handleTickEvent(TickEvent event) {
        long tick = event.getTick();
        LuaTickEvent luaTickEvent = new LuaTickEvent(tick);
        dispatch(luaTickEvent);
    }

    void handleStartWorkerEvent(StartWorkerEvent event) {
        try {
            luaChunk.getChunk().call();
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }

    void handleClientCreatedEvent(ClientCreatedEvent event) {
        long clientId = event.getClient().getId();
        LuaClientCreatedEvent luaEvent = new LuaClientCreatedEvent(clientId);
        dispatch(luaEvent);
    }

    void handleMessageReceivedEvent(MessageReceivedEvent event) {
        long clientId = event.getClient().getId();
        String message = event.getMessage();
        LuaMessageReceivedEvent luaEvent = new LuaMessageReceivedEvent(clientId, message);
        dispatch(luaEvent);
    }

    void handleClientRemovedEvent(ClientRemovedEvent event) {
        long clientId = event.getClient().getId();
        LuaClientRemovedEvent luaEvent = new LuaClientRemovedEvent(clientId);
        dispatch(luaEvent);
    }

    void dispatch(LuaEvent luaEvent) {
        try {
            luaChunk.getRuntime().dispatch(luaEvent.getId(), luaEvent);
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }
}
