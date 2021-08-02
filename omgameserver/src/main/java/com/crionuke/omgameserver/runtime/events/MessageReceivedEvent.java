package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class MessageReceivedEvent extends AddressedEvent {

    final long clientId;
    final LuaValue luaValue;

    public MessageReceivedEvent(long clientId, Address address, LuaValue luaValue) {
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
        return "MessageReceivedEvent{" +
                "address=" + address +
                ", clientId=" + clientId +
                '}';
    }
}
