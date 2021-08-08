package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.RuntimeDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaError;

public class LuaPlatformTest extends Assertions {

    LuaPlatform luaPlatform;

    @BeforeEach
    void beforeEach() {
        luaPlatform = new LuaPlatform(new RuntimeDispatcher());
    }

    @Test
    void testLoadScript() {
        LuaChunk luaChunk = luaPlatform.loadChunk("return_1.lua");
        assertEquals(1, luaChunk.getChunk().call().checknumber().tolong());
    }

    @Test
    void testLoadFile() {
        String filePath = "return_1.lua";
        LuaChunk luaChunk = luaPlatform.loadChunk(filePath);
        assertEquals(1, luaChunk.getChunk().call().checknumber().tolong());
    }

    @Test
    void testInterruptScript() throws InterruptedException {
        LuaChunk luaChunk = luaPlatform.loadChunk("infinity_loop.lua");
        Thread t = new Thread(() -> {
            try {
                luaChunk.getChunk().call();
            } catch (LuaError e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.interrupt();
        t.join();
    }
}
