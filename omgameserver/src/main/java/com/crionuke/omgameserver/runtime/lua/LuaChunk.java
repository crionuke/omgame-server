package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.lua.events.LuaEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaInitEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaChunk {
    static final Logger LOG = Logger.getLogger(LuaChunk.class);

    final Globals globals;
    final String filePath;
    final LuaValue luaSelf;

    LuaChunk(Globals globals, String filePath) {
        this.globals = globals;
        this.filePath = filePath;
        luaSelf = LuaTable.tableOf();
    }

    LuaValue call() {
        LuaValue chunkResult = globals.get("dofile").call(LuaValue.valueOf(filePath));
        LOG.infof("Chunk started, filePath=%s", filePath);
        return chunkResult;
    }

    void fireEvent(LuaEvent luaEvent) {
        LuaValue handler = globals.get(luaEvent.getId());
        if (handler.isnil()) {
            LOG.tracef("No handler for event, eventId=%s", luaEvent.getId());
        } else {
            try {
                handler.call(luaSelf, luaEvent);
            } catch (LuaError luaError) {
                LOG.warnf("Dispatch event failed, eventId=%s, reason=%s",
                        luaEvent.getId(), luaError.getMessage(), luaError);
            }
        }
    }
}
