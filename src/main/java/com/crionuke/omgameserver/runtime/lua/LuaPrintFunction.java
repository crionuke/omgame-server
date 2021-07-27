package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class LuaPrintFunction extends VarArgFunction {
    private static final Logger LOG = Logger.getLogger(LuaPrintFunction.class);

    private final LuaValue tostring;

    LuaPrintFunction(Globals globals) {
        tostring = globals.get("tostring");
    }

    public Varargs invoke(Varargs args) {
        StringBuilder result = new StringBuilder();
        for (int i = 1, n = args.narg(); i <= n; i++) {
            if (i > 1) {
                result.append(' ');
            }
            LuaString s = tostring.call(args.arg(i)).strvalue();
            result.append(s.tojstring());
        }
        LOG.info(result);
        return NONE;
    }
}