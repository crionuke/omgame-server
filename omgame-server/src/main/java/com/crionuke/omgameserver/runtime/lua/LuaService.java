package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.runtime.events.ClientConnectedEvent;
import com.crionuke.omgameserver.runtime.events.ClientDisconnectedEvent;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import com.crionuke.omgameserver.runtime.events.MessageDecodedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaInitEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.ConsumeEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class LuaService {
    static final Logger LOG = Logger.getLogger(LuaService.class);

    final LuaPlatform luaPlatform;
    final Map<Address, LuaWorker> routes;

    LuaService(LuaPlatform luaPlatform) {
        this.luaPlatform = luaPlatform;
        routes = new HashMap<>();
    }

    @ConsumeEvent(value = CreateWorkerEvent.TOPIC)
    void handleCreateWorkerEvent(CreateWorkerEvent event) {
        String rootDirectory = event.getRootDirectory();
        String mainScript = event.getMainScript();
        int tickEveryMillis = event.getTickEveryMillis();
        Address address = event.getAddress();
        if (routes.containsKey(address)) {
            LOG.warnf("Address already taken, address=%s, rootDirectory=%s, mainScript=%s",
                    address, rootDirectory, mainScript);
        } else {
            LuaChunk luaChunk = luaPlatform.createChunk(rootDirectory, mainScript);
            try {
                luaChunk.call();
                luaChunk.fireEvent(new LuaInitEvent());
            } catch (LuaError luaError) {
                LOG.warnf("Init worker failed, address=%s, reason=%s", address, luaError.getMessage());
            }
            LuaWorker luaWorker = new LuaWorker(address, luaChunk, tickEveryMillis);
            routes.put(address, luaWorker);
            LOG.infof("Worker created, mainScript=%s, address=%s", mainScript, address);
        }
    }

    @ConsumeEvent(value = ClientConnectedEvent.TOPIC)
    void handleClientConnectedEvent(ClientConnectedEvent event) {
        route(event.getAddress(), worker -> worker
                .handleClientConnectedEvent(event));
    }

    @ConsumeEvent(value = MessageDecodedEvent.TOPIC)
    void handleMessageDecodedEvent(MessageDecodedEvent event) {
        route(event.getAddress(), worker -> worker
                .handleMessageDecodedEvent(event));
    }

    @ConsumeEvent(value = ClientDisconnectedEvent.TOPIC)
    void handleClientDisconnectedEvent(ClientDisconnectedEvent event) {
        route(event.getAddress(), worker -> worker
                .handleClientDisconnectedEvent(event));
    }

    void route(Address address, Consumer<LuaWorker> consumer) {
        if (routes.containsKey(address)) {
            consumer.accept(routes.get(address));
        } else {
            LOG.warnf("Route not found, address=%s", address);
        }
    }
}
