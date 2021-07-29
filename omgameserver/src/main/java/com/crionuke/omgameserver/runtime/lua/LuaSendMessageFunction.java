package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import com.crionuke.omgameserver.runtime.events.SendMessageEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaSendMessageFunction extends TwoArgFunction {
    static final Logger LOG = Logger.getLogger(LuaSendMessageFunction.class);

    final RuntimeDispatcher runtimeDispatcher;

    LuaSendMessageFunction(RuntimeDispatcher runtimeDispatcher) {
        super();
        this.runtimeDispatcher = runtimeDispatcher;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        long clientId = arg1.checkint();
        String message = arg2.checkjstring();
        runtimeDispatcher.fire(new SendMessageEvent(clientId, message));
        return LuaBoolean.TRUE;
    }
}
