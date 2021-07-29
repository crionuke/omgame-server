package com.crionuke.omgameserver.runtime.lua.events;

import org.luaj.vm2.LuaTable;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
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
