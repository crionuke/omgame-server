package com.crionuke.omgameserver.core;

import com.crionuke.omgameserver.runtime.bootstrap.BootstrapService;
import com.crionuke.omgameserver.runtime.json.JsonService;
import com.crionuke.omgameserver.runtime.lua.LuaService;
import com.crionuke.omgameserver.runtime.server.ServerService;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Startup
@ApplicationScoped
public class Deployer {

    public void init(@Observes StartupEvent e, Vertx vertx,
                     BootstrapService bootstrapService, JsonService jsonService, LuaService luaService,
                     ServerService serverService) {
        vertx.deployVerticle(serverService).await().indefinitely();
        vertx.deployVerticle(jsonService).await().indefinitely();
        vertx.deployVerticle(luaService).await().indefinitely();
        vertx.deployVerticle(bootstrapService).await().indefinitely();
    }
}
