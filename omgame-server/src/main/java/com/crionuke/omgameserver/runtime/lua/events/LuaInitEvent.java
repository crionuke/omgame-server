package com.crionuke.omgameserver.runtime.lua.events;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaInitEvent extends LuaEvent {
    static public final String EVENT_ID = "init";

    public LuaInitEvent() {
        super(EVENT_ID);
    }
}
