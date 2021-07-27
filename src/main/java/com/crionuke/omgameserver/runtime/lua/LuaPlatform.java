package com.crionuke.omgameserver.runtime.lua;

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
    static final Logger LOG = Logger.getLogger(LuaPlatform.class);

    final Globals serverGlobal;

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
    }

    public LuaChunk loadScript(String chunkName, String script) {
        LuaRuntime runtime = new LuaRuntime();
        Globals userGlobals = createUserGlobals(runtime);
        LOG.infof("Load script, chunkName=%s, script=%s", chunkName, script);
        LuaValue chunk = serverGlobal.load(script, chunkName, userGlobals);
        return new LuaChunk(userGlobals, runtime, chunk);
    }

    public LuaChunk loadFile(String filePath) {
        LuaRuntime runtime = new LuaRuntime();
        Globals userGlobals = createUserGlobals(runtime);
        LOG.infof("Load file, filePath=%s", filePath);
        LuaValue chunk = serverGlobal.load(serverGlobal.finder.findResource(filePath),
                "@" + filePath, "bt", userGlobals);
        return new LuaChunk(userGlobals, runtime, chunk);
    }

    Globals createUserGlobals(LuaRuntime luaRuntime) {
        Globals globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new JseStringLib());
        globals.load(new JseMathLib());
        // TODO: Loading and compiling scripts from within scripts may also be prohibited
        LoadState.install(globals);
        LuaC.install(globals);
        // Override print function to output through logger
        globals.set("print", new LuaPrintFunction(globals));
        globals.set("runtime", luaRuntime);
        return globals;
    }

    class ReadOnlyLuaTable extends LuaTable {

        public ReadOnlyLuaTable(LuaValue table) {
            presize(table.length(), 0);
            for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                    .next(n.arg1())) {
                LuaValue key = n.arg1();
                LuaValue value = n.arg(2);
                super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
            }
        }

        @Override
        public LuaValue setmetatable(LuaValue metatable) {
            return error("table is read-only");
        }

        @Override
        public void set(int key, LuaValue value) {
            error("table is read-only");
        }

        @Override
        public void rawset(int key, LuaValue value) {
            error("table is read-only");
        }

        @Override
        public void rawset(LuaValue key, LuaValue value) {
            error("table is read-only");
        }

        @Override
        public LuaValue remove(int pos) {
            return error("table is read-only");
        }
    }
}
