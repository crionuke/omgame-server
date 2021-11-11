package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.runtime.events.ClientConnectedEvent;
import com.crionuke.omgameserver.runtime.events.ClientDisconnectedEvent;
import com.crionuke.omgameserver.runtime.events.MessageDecodedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaConnectedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaDisconnectedEvent;
import com.crionuke.omgameserver.runtime.lua.events.LuaReceivedEvent;
import org.jboss.logging.Logger;
import org.luaj.vm2.LuaValue;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaWorker {
    static final Logger LOG = Logger.getLogger(LuaWorker.class);

    final Address address;
    final LuaChunk luaChunk;
    final int tickEveryMillis;

    LuaWorker(Address address, LuaChunk luaChunk, int tickEveryMillis) {
        this.address = address;
        this.luaChunk = luaChunk;
        this.tickEveryMillis = tickEveryMillis;
        LOG.infof("Created, address=%s, tickEveryMillis=%d", address, tickEveryMillis);
    }

    void handleClientConnectedEvent(final ClientConnectedEvent event) {
        long clientId = event.getClientId();
        LuaConnectedEvent luaEvent = new LuaConnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client connected, clientId=%d", clientId);
    }

    void handleMessageDecodedEvent(final MessageDecodedEvent event) {
        long clientId = event.getClientId();
        LuaValue luaValue = event.getLuaValue();
        LuaReceivedEvent luaEvent = new LuaReceivedEvent(clientId, luaValue);
        luaChunk.fireEvent(luaEvent);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Messasge received, clientId=%d, luaValue=%s", clientId, luaValue);
        }
    }

    void handleClientDisconnectedEvent(final ClientDisconnectedEvent event) {
        long clientId = event.getClientId();
        LuaDisconnectedEvent luaEvent = new LuaDisconnectedEvent(clientId);
        luaChunk.fireEvent(luaEvent);
        LOG.debugf("Client disconnected, clientId=%d", clientId);
    }
}
