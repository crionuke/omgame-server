package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.*;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
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
        addressedEvents.filter(event -> event instanceof MessageReceivedEvent)
                .onItem().castTo(MessageReceivedEvent.class)
                .subscribe().with(event -> handleMessageReceivedEvent(event));
    }

    void handleTickEvent(TickEvent event) {
        LuaTable luaEvent = new LuaTable();
        luaEvent.set("id", "tick");
        luaEvent.set("tick", event.getTick());
        dispatch("tick", luaEvent);
    }

    void handleStartWorkerEvent(StartWorkerEvent event) {
        try {
            luaChunk.getChunk().call();
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }

    void handleMessageReceivedEvent(MessageReceivedEvent event) {
        LuaTable luaEvent = new LuaTable();
        luaEvent.set("id", "message_received");
        luaEvent.set("client_id", event.getClient().getId());
        luaEvent.set("message", event.getMessage());
        dispatch("message_received", luaEvent);
    }

    void dispatch(String id, LuaValue luaEvent) {
        try {
            luaChunk.getRuntime().dispatch(id, luaEvent);
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }
}
