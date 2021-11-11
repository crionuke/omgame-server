package com.crionuke.omgameserver.runtime.lua;

import io.vertx.mutiny.core.eventbus.EventBus;
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

    final EventBus eventBus;

    LuaPlatform(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public LuaChunk createChunk(String filePath) {
        return this.createChunk(".", filePath);
    }

    public LuaChunk createChunk(String rootDirectory, String filePath) {
        Globals userGlobals = createUserGlobals();
        LuaRuntime luaRuntime = new LuaRuntime(eventBus, userGlobals);
        userGlobals.set("omgs", luaRuntime);
        userGlobals.finder = new LuaResourceFinder(rootDirectory);
        return new LuaChunk(userGlobals, filePath);
    }

    Globals createUserGlobals() {
        Globals globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new JseStringLib());
        globals.load(new JseMathLib());
        LoadState.install(globals);
        LuaC.install(globals);
        // Override print function to output through logger
        globals.set("print", new LuaPrintFunction(globals));
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
