package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaLogFunction extends VarArgFunction {
    static final Logger LOG = Logger.getLogger(LuaEventListener.class);

    private final LEVEL logLevel;
    private final LuaValue tostring;

    LuaLogFunction(Globals globals, LEVEL logLevel) {
        this.logLevel = logLevel;
        tostring = globals.get("tostring");
    }

    @Override
    public Varargs invoke(Varargs args) {
        StringBuilder result = new StringBuilder();
        for (int i = 1, n = args.narg(); i <= n; i++) {
            if (i > 1) {
                result.append(' ');
            }
            LuaString s = tostring.call(args.arg(i)).strvalue();
            result.append(s.tojstring());
        }
        switch (logLevel) {
            case ERROR:
                LOG.error(result.toString());
                break;
            case WARN:
                LOG.warn(result.toString());
                break;
            case INFO:
                LOG.info(result.toString());
                break;
            case DEBUG:
                LOG.debug(result.toString());
                break;
            case TRACE:
                LOG.trace(result.toString());
                break;
        }
        return NONE;
    }

    enum LEVEL {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
    }
}
