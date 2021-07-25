package com.crionuke.omgameserver.lua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaPlatformTest extends Assertions {

    LuaPlatform luaPlatform;

    @BeforeEach
    void beforeEach() {
        luaPlatform = new LuaPlatform();
    }

    @Test
    void testLoadScript() {
        String script = "return 1";
        LuaValue chunk = luaPlatform.loadScript("return1", script);
        assertEquals(1, chunk.call().checknumber().tolong());
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
        LuaValue chunk = luaPlatform.loadScript("testInterruptScript", script);
        Thread t = new Thread(() -> {
            try {
                chunk.call();
            } catch (LuaError e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.interrupt();
        t.join();
    }
}
