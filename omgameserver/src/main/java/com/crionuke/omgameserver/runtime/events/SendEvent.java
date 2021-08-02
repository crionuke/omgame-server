package com.crionuke.omgameserver.runtime.events;

import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class SendEvent extends RuntimeEvent {

    final long clientId;
    final LuaValue luaValue;

    public SendEvent(long clientId, LuaValue luaValue) {
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
        return "SendMessageEvent{" +
                "clientId=" + clientId +
                '}';
    }
}
