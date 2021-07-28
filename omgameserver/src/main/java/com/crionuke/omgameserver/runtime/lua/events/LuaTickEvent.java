package com.crionuke.omgameserver.runtime.lua.events;

public class LuaTickEvent extends LuaEvent {
    static public final String TICK_EVENT_ID = "tick";

    public LuaTickEvent(long tick) {
        super(TICK_EVENT_ID);
        set("tick", tick);
    }
}
