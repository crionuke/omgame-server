package com.crionuke.omgameserver.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(LuaWorker.class);

    private final String chunkName;
    private final LuaValue luaChunk;

    LuaWorker(String chunkName, LuaValue luaChunk) {
        this.chunkName = chunkName;
        this.luaChunk = luaChunk;
        LOG.infof("Created, chunkName=%s", chunkName);
    }

    @Override
    public void run() {
        String threadOldName = Thread.currentThread().getName();
        Thread.currentThread().setName(chunkName);
        try {
            // TODO: infinity loop, events, etc
            luaChunk.call();
        } catch (LuaError luaError) {
            LOG.infof("Lua chunk failed, name=%s, reason=%s", chunkName, luaError.getMessage());
        } finally {
            Thread.currentThread().setName(threadOldName);
        }
    }
}
