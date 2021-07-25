package com.crionuke.omgameserver.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaPlatform {
    private static final Logger LOG = Logger.getLogger(LuaPlatform.class);

    private final Globals serverGlobal;

    LuaPlatform() {
        // Create server globals with just enough library support to compile user scripts.
        serverGlobal = new Globals();
        serverGlobal.load(new JseBaseLib());
        serverGlobal.load(new JseMathLib());
        serverGlobal.load(new JseStringLib());
        LoadState.install(serverGlobal);
        LuaC.install(serverGlobal);
        // Set up the LuaString metatable to be read-only since it is shared across all scripts.
        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
        LOG.info("Created");
    }

    public LuaValue loadScript(String chunkName, String script) {
        Globals userGlobals = createUserGlobals();
        LOG.infof("Load script, chunkName=%s, script=%s", chunkName, script);
        return serverGlobal.load(script, chunkName, userGlobals);
    }

    public LuaValue loadFile(String filePath) {
        Globals userGlobals = createUserGlobals();
        LOG.infof("Load file, filePath=%s", filePath);
        return serverGlobal.load(serverGlobal.finder.findResource(filePath),
                "@" + filePath, "bt", userGlobals);
    }

    private Globals createUserGlobals() {
        Globals globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new JseStringLib());
        globals.load(new JseMathLib());
        // Override print function to output through logger
        globals.set("print", new LuaPrintFunction(globals));
        return globals;
    }

    static private class ReadOnlyLuaTable extends LuaTable {
        public ReadOnlyLuaTable(LuaValue table) {
            presize(table.length(), 0);
            for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                    .next(n.arg1())) {
                LuaValue key = n.arg1();
                LuaValue value = n.arg(2);
                super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
            }
        }

        public LuaValue setmetatable(LuaValue metatable) {
            return error("table is read-only");
        }

        public void set(int key, LuaValue value) {
            error("table is read-only");
        }

        public void rawset(int key, LuaValue value) {
            error("table is read-only");
        }

        public void rawset(LuaValue key, LuaValue value) {
            error("table is read-only");
        }

        public LuaValue remove(int pos) {
            return error("table is read-only");
        }
    }
}
