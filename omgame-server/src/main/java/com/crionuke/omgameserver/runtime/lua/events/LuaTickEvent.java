package com.crionuke.omgameserver.runtime.lua.events;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaTickEvent extends LuaEvent {
    static public final String TICK_EVENT_ID = "tick";

    public LuaTickEvent(long tick, long time) {
        super(TICK_EVENT_ID);
        set("tick", tick);
        set("time", time);
    }
}
