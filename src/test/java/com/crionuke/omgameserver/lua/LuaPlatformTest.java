package com.crionuke.omgameserver.lua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;

public class LuaPlatformTest extends Assertions {

    LuaPlatform luaPlatform;

    @BeforeEach
    public void before() {
        luaPlatform = new LuaPlatform();
    }

    @Test
    public void testLoadScript() {
        String script = "return 1";
        LuaValue chunk = luaPlatform.loadScript("return1", script);
        assertEquals(1, chunk.call().checknumber().tolong());
    }
}
