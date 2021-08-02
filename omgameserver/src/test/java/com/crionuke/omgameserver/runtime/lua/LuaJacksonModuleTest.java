package com.crionuke.omgameserver.runtime.lua;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;

public class LuaJacksonModuleTest extends Assertions {

    @Test
    void testSerializer() throws JsonProcessingException {
        LuaPlatform luaPlatform = new LuaPlatform();
        String script = """
                return {
                    logical = {
                        boolean1 = true,
                        boolean2 = false
                    },
                    numbers = {1024, 3.1415, {-128, -3.14}},
                    string = "value",                   
                }
                """;
        LuaChunk luaChunk = luaPlatform.loadScript("table", script);
        LuaValue luaValue = luaChunk.chunk.call();
        // Serializer
        ObjectMapper objectMapper = new ObjectMapper();
        LuaJacksonModule luaJacksonModule = new LuaJacksonModule();
        luaJacksonModule.customize(objectMapper);
        String valueAsString = objectMapper.writeValueAsString(luaValue);
        JsonNode root = objectMapper.readTree(valueAsString);
        // Assertions
        JsonNode logical = root.get("logical");
        assertEquals(true, logical.get("boolean1").booleanValue());
        assertEquals(false, logical.get("boolean2").booleanValue());
        JsonNode numbers = root.get("numbers");
        assertEquals(1024, numbers.get(0).longValue());
        assertEquals(3.1415, numbers.get(1).doubleValue());
        JsonNode subArray = numbers.get(2);
        assertEquals(-128, subArray.get(0).longValue());
        assertEquals(-3.14, subArray.get(1).doubleValue());
    }

    @Test
    void testDeserializer() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        LuaJacksonModule luaJacksonModule = new LuaJacksonModule();
        luaJacksonModule.customize(objectMapper);
        String jsonString = """
                {
                    "logical": {
                        "boolean1": true,
                        "boolean2": false
                    },
                    "numbers": [1024, 3.1415, [-128, -3.14]],
                    "string": "value"
                }
                """;
        LuaValue luaValue = objectMapper.readValue(jsonString, LuaValue.class);
        assertEquals(true, luaValue.get("logical").get("boolean1").toboolean());
        assertEquals(false, luaValue.get("logical").get("boolean2").toboolean());
        assertEquals(1024, luaValue.get("numbers").get(1).tolong());
        assertEquals(3.1415, luaValue.get("numbers").get(2).todouble());
        assertEquals(-128, luaValue.get("numbers").get(3).get(1).tolong());
        assertEquals(-3.14, luaValue.get("numbers").get(3).get(2).todouble());
        assertEquals("value", luaValue.get("string").tojstring());
    }
}
