package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.StartWorkerEvent;
import com.crionuke.omgameserver.runtime.events.RuntimeEvent;
import com.crionuke.omgameserver.runtime.events.TickEvent;
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
        Multi<RuntimeEvent> events = runtimeDispatcher.getMulti()
                .emitOn(getSelfExecutor());
        events.filter(event -> event instanceof StartWorkerEvent)
                .onItem().castTo(StartWorkerEvent.class)
                .filter(event -> event.getAddress().equals(address))
                .log().subscribe().with(event -> handleStartWorkerEvent(event));
        events.filter(event -> event instanceof TickEvent)
                .onItem().castTo(TickEvent.class)
                .subscribe().with(event -> handleTickEvent(event));
    }

    void handleStartWorkerEvent(StartWorkerEvent event) {
        try {
            luaChunk.getChunk().call();
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }

    void handleTickEvent(TickEvent event) {
        LuaTable luaEvent = new LuaTable();
        luaEvent.set("id", "tick");
        luaEvent.set("tick", event.getTick());
        dispatch("tick", luaEvent);
    }

    void dispatch(String id, LuaValue luaEvent) {
        try {
            luaChunk.getRuntime().dispatch(id, luaEvent);
        } catch (LuaError luaError) {
            LOG.warnf("Worker failed, address=%s, reason=%s", address.asPath(), luaError.getMessage());
        }
    }
}
