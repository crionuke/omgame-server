package com.crionuke.omgameserver.runtime.lua;

import io.vertx.mutiny.core.Vertx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaError;

public class LuaPlatformTest extends Assertions {

    LuaPlatform luaPlatform;

    @BeforeEach
    void beforeEach() {
        luaPlatform = new LuaPlatform(Vertx.vertx().eventBus());
    }

    @Test
    void testLoadScript() {
        LuaChunk luaChunk = luaPlatform.createChunk("return_1.lua");
        assertEquals(1, luaChunk.call().checknumber().tolong());
    }

    @Test
    void testLoadFile() {
        String filePath = "return_1.lua";
        LuaChunk luaChunk = luaPlatform.createChunk(filePath);
        assertEquals(1, luaChunk.call().checknumber().tolong());
    }

    @Test
    void testInterruptScript() throws InterruptedException {
        LuaChunk luaChunk = luaPlatform.createChunk("infinity_loop.lua");
        Thread t = new Thread(() -> {
            try {
                luaChunk.call();
            } catch (LuaError e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.interrupt();
        t.join();
    }
}
