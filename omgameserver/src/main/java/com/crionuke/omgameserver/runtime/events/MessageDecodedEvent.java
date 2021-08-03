package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;
import org.luaj.vm2.LuaValue;

public class MessageDecodedEvent extends AddressedEvent {

    final long clientId;
    final LuaValue luaValue;

    public MessageDecodedEvent(Address address, long clientId, LuaValue luaValue) {
        super(address);
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
        return "JsonMessageDecodedEvent{" +
                "address=" + address +
                ", clientId=" + clientId +
                '}';
    }
}
