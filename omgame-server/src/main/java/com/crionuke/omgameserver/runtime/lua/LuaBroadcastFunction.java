package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.events.BroadcastLuaValueEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
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

    final EventBus eventBus;

    LuaBroadcastFunction(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
    }

    @Override
    public LuaValue call(LuaValue arg) {
        eventBus.publish(BroadcastLuaValueEvent.TOPIC, new BroadcastLuaValueEvent(arg));
        return LuaBoolean.TRUE;
    }
}
