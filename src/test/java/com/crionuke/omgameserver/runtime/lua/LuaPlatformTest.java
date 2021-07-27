package com.crionuke.omgameserver.runtime.lua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaError;

public class LuaPlatformTest extends Assertions {

    LuaPlatform luaPlatform;

    @BeforeEach
    void beforeEach() {
        luaPlatform = new LuaPlatform();
    }

    @Test
    void testLoadScript() {
        String script = "return 1";
        LuaChunk luaChunk = luaPlatform.loadScript("return1", script);
        assertEquals(1, luaChunk.getChunk().call().checknumber().tolong());
    }

    @Test
    void testLoadFile() {
        String filePath = "return1.lua";
        LuaChunk luaChunk = luaPlatform.loadFile(filePath);
        assertEquals(1, luaChunk.getChunk().call().checknumber().tolong());
    }

    @Test
    void testInterruptScript() throws InterruptedException {
        // Lua infinity script
        String script = """
                local i = 0;
                while true do
                    i = i + 1
                end
                """;
        LuaChunk luaChunk = luaPlatform.loadScript("testInterruptScript", script);
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
