package com.crionuke.omgameserver.runtime.lua.events;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaConnectedEvent extends LuaEvent {
    static public final String EVENT_ID = "connected";

    public LuaConnectedEvent(long clientId) {
        super(EVENT_ID);
        set("client_id", clientId);
    }
}
