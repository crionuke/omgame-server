package com.crionuke.omgameserver.runtime.lua.events;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaClientRemovedEvent extends LuaEvent {
    static public final String CLIENT_REMOVED_EVENT_ID = "client_removed";

    public LuaClientRemovedEvent(long clientId) {
        super(CLIENT_REMOVED_EVENT_ID);
        set("client_id", clientId);
    }
}
