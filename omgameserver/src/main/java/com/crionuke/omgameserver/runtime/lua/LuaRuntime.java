package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import org.jboss.logging.Logger;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaRuntime extends LuaEventListener {
    static final Logger LOG = Logger.getLogger(LuaRuntime.class);

    final String FUNCTION_SEND_MESSAGE = "send_message";

    LuaRuntime(RuntimeDispatcher runtimeDispatcher) {
        super();
        set(FUNCTION_SEND_MESSAGE, new LuaSendMessageFunction(runtimeDispatcher));
    }
}
