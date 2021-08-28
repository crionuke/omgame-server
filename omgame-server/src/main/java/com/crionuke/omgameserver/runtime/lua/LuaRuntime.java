package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import org.jboss.logging.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaRuntime extends LuaTable {
    static final Logger LOG = Logger.getLogger(LuaRuntime.class);

    final String FUNCTION_UNICAST = "unicast";
    final String FUNCTION_BROADCAST = "broadcast";

    final String FUNCTION_LOG_ERROR = "log_error";
    final String FUNCTION_LOG_WARN = "log_warn";
    final String FUNCTION_LOG_INFO = "log_info";
    final String FUNCTION_LOG_DEBUG = "log_debug";
    final String FUNCTION_LOG_TRACE = "log_trace";

    LuaRuntime(RuntimeDispatcher runtimeDispatcher, Globals globals) {
        super();

        set(FUNCTION_UNICAST, new LuaUnicastFunction(runtimeDispatcher));
        set(FUNCTION_BROADCAST, new LuaBroadcastFunction(runtimeDispatcher));

        set(FUNCTION_LOG_ERROR, new LuaLogFunction(globals, LuaLogFunction.LEVEL.ERROR));
        set(FUNCTION_LOG_WARN, new LuaLogFunction(globals, LuaLogFunction.LEVEL.WARN));
        set(FUNCTION_LOG_INFO, new LuaLogFunction(globals, LuaLogFunction.LEVEL.INFO));
        set(FUNCTION_LOG_DEBUG, new LuaLogFunction(globals, LuaLogFunction.LEVEL.DEBUG));
        set(FUNCTION_LOG_TRACE, new LuaLogFunction(globals, LuaLogFunction.LEVEL.TRACE));
    }
}
