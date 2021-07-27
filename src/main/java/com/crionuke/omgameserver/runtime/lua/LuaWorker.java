package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker implements Runnable {
    static final Logger LOG = Logger.getLogger(LuaWorker.class);

    final Address address;
    final LuaValue luaChunk;

    LuaWorker(Address address, LuaValue luaChunk) {
        this.address = address;
        this.luaChunk = luaChunk;
        LOG.infof("Created, address=%s", address);
    }

    @Override
    public void run() {
        String addressAsPath = address.asPath();
        String threadOldName = Thread.currentThread().getName();
        Thread.currentThread().setName(addressAsPath);
        try {
            // TODO: infinity loop, events, etc
            luaChunk.call();
        } catch (LuaError luaError) {
            LOG.infof("Lua chunk failed, name=%s, reason=%s", addressAsPath, luaError.getMessage());
        } finally {
            Thread.currentThread().setName(threadOldName);
        }
    }
}
