package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.SendEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaSendFunction extends TwoArgFunction {
    static final Logger LOG = Logger.getLogger(LuaSendFunction.class);

    final RuntimeDispatcher runtimeDispatcher;

    LuaSendFunction(RuntimeDispatcher runtimeDispatcher) {
        super();
        this.runtimeDispatcher = runtimeDispatcher;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        long clientId = arg1.checkint();
        runtimeDispatcher.fire(new SendEvent(clientId, arg2));
        return LuaBoolean.TRUE;
    }
}
