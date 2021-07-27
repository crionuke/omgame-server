package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Map;
import java.util.Set;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaRemoveEventListenerFunction extends TwoArgFunction {
    static final Logger LOG = Logger.getLogger(LuaRemoveEventListenerFunction.class);

    final Map<LuaString, Set<LuaFunction>> functional;
    final Map<LuaString, Set<LuaTable>> tabulated;

    LuaRemoveEventListenerFunction(Map<LuaString, Set<LuaFunction>> functional, Map<LuaString, Set<LuaTable>> tabulated) {
        super();
        this.functional = functional;
        this.tabulated = tabulated;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        if (arg1.isstring()) {
            LuaString key = arg1.checkstring();
            if (arg2.isfunction()) {
                Set<LuaFunction> functions = functional.get(key);
                if (functions != null) {
                    functions.remove(arg2.checkfunction());
                    LOG.debugf("Remove functional events listener, key=%s", key.toString());
                    return LuaBoolean.TRUE;
                }
            } else if (arg2.istable()) {
                Set<LuaTable> tables = tabulated.get(key);
                if (tables != null) {
                    tables.remove(arg2.checktable());
                    LOG.debugf("Remove tabulated events listener, key=%s", key.toString());
                    return LuaBoolean.TRUE;
                }
            }
        }
        return LuaBoolean.FALSE;
    }
}
