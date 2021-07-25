package com.crionuke.omgameserver.lua;

import com.crionuke.omgameserver.websocket.WebSocketEventStream;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

class LuaWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(WebSocketEventStream.class);

    private final String chunkName;
    private final LuaValue luaChunk;

    LuaWorker(String chunkName, LuaValue luaChunk) {
        this.chunkName = chunkName;
        this.luaChunk = luaChunk;
        LOG.infof("Created, chunkName=%s", chunkName);
    }

    @Override
    public void run() {
        try {
            // TODO: infinity loop, events, etc
            luaChunk.call();
        } catch (LuaError luaError) {
            LOG.infof("Lua chunk failed, name=%s, reason=%s", chunkName, luaError.getMessage());
        }
    }
}
