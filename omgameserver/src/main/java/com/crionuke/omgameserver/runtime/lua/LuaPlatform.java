package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import org.jboss.logging.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@ApplicationScoped
class LuaPlatform {
    static final Logger LOG = Logger.getLogger(LuaPlatform.class);

    final RuntimeDispatcher runtimeDispatcher;

    LuaPlatform(RuntimeDispatcher runtimeDispatcher) {
        this.runtimeDispatcher = runtimeDispatcher;
    }

    public LuaChunk loadChunk(String filePath) {
        return this.loadChunk(".", filePath);
    }

    public LuaChunk loadChunk(String rootDirectory, String filePath) {
        LuaRuntime runtime = new LuaRuntime(runtimeDispatcher);
        Globals userGlobals = createUserGlobals(runtime);
        userGlobals.finder = new LuaResourceFinder(rootDirectory);
        LOG.infof("Load file, rootDirectory=%s, filePath=%s", rootDirectory, filePath);
        LuaValue chunk = userGlobals.load(userGlobals.finder.findResource(filePath),
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
        globals.set("omgs", luaRuntime);
        // Set up the LuaString metatable to be read-only since it is shared across all scripts.
        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
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
