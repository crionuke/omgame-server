package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.bootstrap.Bootstrap;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import com.crionuke.omgameserver.runtime.events.StartWorkerEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class LuaService extends Handler {
    static final Logger LOG = Logger.getLogger(LuaService.class);

    final Bootstrap bootstrap;
    final RuntimeDispatcher runtimeDispatcher;
    final LuaPlatform luaPlatform;
    final Map<Address, LuaWorker> routes;

    LuaService(Bootstrap bootstrap, RuntimeDispatcher runtimeDispatcher) {
        super(LuaService.class.getSimpleName());
        this.bootstrap = bootstrap;
        this.runtimeDispatcher = runtimeDispatcher;
        luaPlatform = new LuaPlatform(runtimeDispatcher);
        routes = new HashMap<>();
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<Event> events = Multi.createBy().concatenating()
                // Handle first bootstrap events, next runtime
                .streams(bootstrap.getMulti(), runtimeDispatcher.getMulti())
                .emitOn(getSelfExecutor());
        events.filter(event -> event instanceof CreateWorkerEvent)
                .onItem().castTo(CreateWorkerEvent.class).subscribe().with(event -> handleCreateWorkerEvent(event));
    }

    void handleCreateWorkerEvent(CreateWorkerEvent event) {
        String script = event.getScript();
        Address address = event.getAddress();
        if (routes.containsKey(address)) {
            LOG.warnf("Address already taken, address='%s', script='%s'", address, script);
        } else {
            LuaChunk luaChunk = luaPlatform.loadFile(script);
            LuaWorker luaWorker = new LuaWorker(address, luaChunk, runtimeDispatcher);
            luaWorker.postConstruct();
            routes.put(address, luaWorker);
            runtimeDispatcher.fire(new StartWorkerEvent(address));
            LOG.infof("Worker created, script=%s, address=%s", script, address);
        }
    }
}
