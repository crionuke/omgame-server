package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Event;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class UnicastLuaValueEvent extends Event {

    public static final String TOPIC = "UnicastLuaValueEvent";

    final long clientId;
    final LuaValue luaValue;

    public UnicastLuaValueEvent(long clientId, LuaValue luaValue) {
        this.clientId = clientId;
        this.luaValue = luaValue;
    }

    public long getClientId() {
        return clientId;
    }

    public LuaValue getLuaValue() {
        return luaValue;
    }

    @Override
    public String toString() {
        return "SendLuaValueEvent{" +
                "clientId=" + clientId +
                '}';
    }
}
