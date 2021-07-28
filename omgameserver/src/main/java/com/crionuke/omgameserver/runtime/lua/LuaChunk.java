package com.crionuke.omgameserver.runtime.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaChunk {

    final Globals globals;
    final LuaRuntime runtime;
    final LuaValue chunk;

    LuaChunk(Globals globals, LuaRuntime runtime, LuaValue chunk) {
        this.globals = globals;
        this.runtime = runtime;
        this.chunk = chunk;
    }

    Globals getGlobals() {
        return globals;
    }

    LuaRuntime getRuntime() {
        return runtime;
    }

    LuaValue getChunk() {
        return chunk;
    }
}
