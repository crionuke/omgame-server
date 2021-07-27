package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaAddEventListenerFunction extends TwoArgFunction {
    static final Logger LOG = Logger.getLogger(LuaAddEventListenerFunction.class);

    final Map<LuaString, Set<LuaFunction>> functional;
    final Map<LuaString, Set<LuaTable>> tabulated;

    LuaAddEventListenerFunction(Map<LuaString, Set<LuaFunction>> functional, Map<LuaString, Set<LuaTable>> tabulated) {
        super();
        this.functional = functional;
        this.tabulated = tabulated;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        if (arg1.isstring()) {
            LuaString id = arg1.checkstring();
            if (arg2.isfunction()) {
                Set<LuaFunction> functions = functional.get(id);
                if (functions == null) {
                    functions = new HashSet<>();
                    functional.put(id, functions);
                }
                functions.add(arg2.checkfunction());
                LOG.debugf("Add functional events listener, id=%s", id.toString());
                return LuaBoolean.TRUE;
            } else if (arg2.istable()) {
                Set<LuaTable> tables = tabulated.get(id);
                if (tables == null) {
                    tables = new HashSet<>();
                    tabulated.put(id, tables);
                }
                tables.add(arg2.checktable());
                LOG.debugf("Add tabulated events listener, id=%s", id.toString());
                return LuaBoolean.TRUE;
            }
        }
        return LuaBoolean.FALSE;
    }
}
