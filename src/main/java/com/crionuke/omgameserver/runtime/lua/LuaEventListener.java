package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaEventListener extends LuaTable {
    static final Logger LOG = Logger.getLogger(LuaEventListener.class);

    final String FUNCTION_ADD_EVENT_LISTENER = "add_event_listener";
    final String FUNCTION_REMOVE_EVENT_LISTENER = "remove_event_listener";

    final Map<LuaString, Set<LuaFunction>> functional;
    final Map<LuaString, Set<LuaTable>> tabulated;

    LuaEventListener() {
        super();
        functional = new HashMap<>();
        tabulated = new HashMap<>();
        set(FUNCTION_ADD_EVENT_LISTENER, new LuaAddEventListenerFunction(functional, tabulated));
        set(FUNCTION_REMOVE_EVENT_LISTENER, new LuaRemoveEventListenerFunction(functional, tabulated));
    }

    void dispatch(String id, LuaValue event) {
        LuaString key = LuaString.valueOf(id);
        Set<LuaFunction> functions = functional.get(key);
        int count = 0;
        if (functions != null) {
            for (LuaFunction luaFunction : functions) {
                luaFunction.call(event);
                count++;
            }
        }
        Set<LuaTable> tables = tabulated.get(key);
        if (tables != null) {
            for (LuaTable luaTable : tables) {
                LuaValue tableFunction = luaTable.get(key);
                tableFunction.call(luaTable, event);
                count++;
            }
        }
        LOG.tracef("Dispatch, id=%s, count=%d", id, count);
    }
}
