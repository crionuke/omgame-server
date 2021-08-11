package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.bootstrap.BootstrapService;
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

    final BootstrapService bootstrapService;
    final RuntimeDispatcher runtimeDispatcher;
    final LuaPlatform luaPlatform;
    final Map<Address, LuaWorker> routes;

    LuaService(BootstrapService bootstrapService, LuaPlatform luaPlatform, RuntimeDispatcher runtimeDispatcher) {
        super(LuaService.class.getSimpleName());
        this.bootstrapService = bootstrapService;
        this.luaPlatform = luaPlatform;
        this.runtimeDispatcher = runtimeDispatcher;
        routes = new HashMap<>();
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<Event> events = Multi.createBy().concatenating()
                // Handle first bootstrap events, next runtime
                .streams(bootstrapService.getMulti(), runtimeDispatcher.getMulti())
                .emitOn(getSelfExecutor());
        subscribe(events, CreateWorkerEvent.class, this::handleCreateWorkerEvent);
    }

    void handleCreateWorkerEvent(CreateWorkerEvent event) {
        String rootDirectory = event.getRootDirectory();
        String mainScript = event.getMainScript();
        int tickEveryMillis = event.getTickEveryMillis();
        Address address = event.getAddress();
        if (routes.containsKey(address)) {
            LOG.warnf("Address already taken, address=%s, rootDirectory=%s, mainScript=%s",
                    address, rootDirectory, mainScript);
        } else {
            LuaChunk luaChunk = luaPlatform.loadChunk(rootDirectory, mainScript);
            LuaWorker luaWorker = new LuaWorker(address, luaChunk, runtimeDispatcher, tickEveryMillis);
            luaWorker.postConstruct();
            routes.put(address, luaWorker);
            runtimeDispatcher.fire(new StartWorkerEvent(address));
            LOG.infof("Worker created, mainScript=%s, address=%s", mainScript, address);
        }
    }
}
