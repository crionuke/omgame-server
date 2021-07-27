package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.bootstrap.Bootstrap;
import com.crionuke.omgameserver.runtime.events.RunWorkerEvent;
import com.crionuke.omgameserver.runtime.events.RuntimeEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class LuaHandler extends Handler {
    static final Logger LOG = Logger.getLogger(LuaHandler.class);

    final Bootstrap bootstrap;
    final RuntimeDispatcher runtimeDispatcher;
    final LuaPlatform luaPlatform;
    final Map<Address, LuaWorker> routes;
    final ExecutorService workersExecutor;

    LuaHandler(Bootstrap bootstrap, RuntimeDispatcher runtimeDispatcher) {
        super(LuaHandler.class.getSimpleName());
        this.bootstrap = bootstrap;
        this.runtimeDispatcher = runtimeDispatcher;
        luaPlatform = new LuaPlatform();
        routes = new HashMap<>();
        workersExecutor = Executors.newCachedThreadPool();
        LOG.infof("Created");
    }

    @PostConstruct
    void postConstruct() {
        Multi<RuntimeEvent> events = Multi.createBy().concatenating()
                // Handle first bootstrap events, next runtime
                .streams(bootstrap.getMulti(), runtimeDispatcher.getMulti())
                .emitOn(getSelfExecutor());
        events.filter(event -> event instanceof RunWorkerEvent)
                .onItem().castTo(RunWorkerEvent.class).log().subscribe().with(event -> handleRunWorkerEvent(event));
    }

    void handleRunWorkerEvent(RunWorkerEvent event) {
        String script = event.getScript();
        Address address = event.getAddress();
        if (routes.containsKey(address)) {
            LOG.warnf("Address already taken, address='%s', script='%s'", address, script);
        } else {
            LuaValue luaChunk = luaPlatform.loadFile(script);
            LuaWorker luaWorker = new LuaWorker(address, luaChunk);
            routes.put(address, luaWorker);
            workersExecutor.execute(luaWorker);
            LOG.warnf("Worker executed, address='%s', script='%s'", address, script);
        }
    }
}
