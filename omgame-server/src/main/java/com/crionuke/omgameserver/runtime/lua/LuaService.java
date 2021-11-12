package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Handler;
import com.crionuke.omgameserver.runtime.events.ClientConnectedEvent;
import com.crionuke.omgameserver.runtime.events.ClientDisconnectedEvent;
import com.crionuke.omgameserver.runtime.events.CreateWorkerEvent;
import com.crionuke.omgameserver.runtime.events.MessageDecodedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaInitEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.DeploymentOptions;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;

import javax.enterprise.context.ApplicationScoped;


/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class LuaService extends Handler {
    static final Logger LOG = Logger.getLogger(LuaService.class);

    final Vertx vertx;
    final LuaPlatform luaPlatform;

    LuaService(Vertx vertx, LuaPlatform luaPlatform) {
        super("lua");
        this.vertx = vertx;
        this.luaPlatform = luaPlatform;
    }

    @ConsumeEvent(value = CreateWorkerEvent.TOPIC, blocking = true)
    void handleCreateWorkerEvent(final CreateWorkerEvent event) {
        Address address = event.getAddress();
        String rootDirectory = event.getRootDirectory();
        String mainScript = event.getMainScript();
        int tickEveryMillis = event.getTickEveryMillis();
        LuaChunk luaChunk = luaPlatform.createChunk(rootDirectory, mainScript);
        try {
            luaChunk.call();
            luaChunk.fireEvent(new LuaInitEvent());
            LuaWorker luaWorker = new LuaWorker(address, luaChunk, tickEveryMillis);
            LOG.infof("Worker created, mainScript=%s, address=%s", mainScript, address);
            DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true);
            vertx.deployVerticle(luaWorker, deploymentOptions)
                    .await().indefinitely();
        } catch (LuaError luaError) {
            LOG.warnf("Init worker failed, address=%s, reason=%s", address, luaError.getMessage());
        }
    }

    @ConsumeEvent(value = ClientConnectedEvent.TOPIC)
    void handleClientConnectedEvent(ClientConnectedEvent event) {
        vertx.eventBus().publish(event.getAddress().toString(), event);
    }

    @ConsumeEvent(value = MessageDecodedEvent.TOPIC)
    void handleMessageDecodedEvent(MessageDecodedEvent event) {
        vertx.eventBus().publish(event.getAddress().toString(), event);
    }

    @ConsumeEvent(value = ClientDisconnectedEvent.TOPIC)
    void handleClientDisconnectedEvent(ClientDisconnectedEvent event) {
        vertx.eventBus().publish(event.getAddress().toString(), event);
    }
}
