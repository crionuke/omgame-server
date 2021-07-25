package com.crionuke.omgameserver.lua;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Startup
@ApplicationScoped
public class LuaService {
    private static final Logger LOG = Logger.getLogger(LuaService.class);

    private final LuaPlatform luaPlatform;
    private final ExecutorService executorService;
    private final LuaWorker luaWorker;

    LuaService(
            @ConfigProperty(name = "omgameserver.luaRuntime.script", defaultValue = "server.lua") String luaScript) {
        luaPlatform = new LuaPlatform();
        LuaValue luaChunk = luaPlatform.loadFile(luaScript);
        // TODO: use fixed size
        executorService = Executors.newCachedThreadPool();
        luaWorker = new LuaWorker(luaScript, luaChunk);
        executorService.execute(luaWorker);
        LOG.infof("Created, luaScript=%s", luaScript);
    }
}
