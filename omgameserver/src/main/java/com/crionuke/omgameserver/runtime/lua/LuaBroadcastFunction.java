package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.BroadcastLuaValueEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaBroadcastFunction extends OneArgFunction {
    static final Logger LOG = Logger.getLogger(LuaBroadcastFunction.class);

    final RuntimeDispatcher runtimeDispatcher;

    LuaBroadcastFunction(RuntimeDispatcher runtimeDispatcher) {
        super();
        this.runtimeDispatcher = runtimeDispatcher;
    }

    @Override
    public LuaValue call(LuaValue arg) {
        runtimeDispatcher.fire(new BroadcastLuaValueEvent(arg));
        return LuaBoolean.TRUE;
    }
}
