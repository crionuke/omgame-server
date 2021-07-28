package com.crionuke.omgameserver.runtime.lua.events;

import org.luaj.vm2.LuaTable;

public abstract class LuaEvent extends LuaTable {

    final String id;

    public LuaEvent(String id) {
        this.id = id;
        set("id", id);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "LuaEvent{" +
                "id='" + id + '\'' +
                '}';
    }
}
