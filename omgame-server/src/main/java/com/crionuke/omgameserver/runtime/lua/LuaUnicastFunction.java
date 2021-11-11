package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.events.UnicastLuaValueEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaUnicastFunction extends TwoArgFunction {
    static final Logger LOG = Logger.getLogger(LuaUnicastFunction.class);

    final EventBus eventBus;

    LuaUnicastFunction(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        long clientId = arg1.checkint();
        eventBus.publish(UnicastLuaValueEvent.TOPIC, new UnicastLuaValueEvent(clientId, arg2));
        return LuaBoolean.TRUE;
    }
}
