package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class BroadcastLuaValueEvent extends Event {

    public static final String TOPIC = "BroadcastLuaValueEvent";

    final LuaValue luaValue;

    public BroadcastLuaValueEvent(LuaValue luaValue) {
        this.luaValue = luaValue;
    }

    public LuaValue getLuaValue() {
        return luaValue;
    }

    @Override
    public String toString() {
        return "BroadcastLuaValueEvent{}";
    }
}
