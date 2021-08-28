package com.crionuke.omgameserver.runtime.lua.events;

import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaReceivedEvent extends LuaEvent {
    static public final String EVENT_ID = "received";

    public LuaReceivedEvent(long clientId, LuaValue luaValue) {
        super(EVENT_ID);
        set("client_id", clientId);
        set("data", luaValue);
    }
}
