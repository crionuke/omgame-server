package com.crionuke.omgameserver.runtime.lua;

import com.crionuke.omgameserver.runtime.json.LuaJacksonModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LuaJacksonModuleTest extends Assertions {

    @Test
    void testSerializer() throws JsonProcessingException {
        LuaPlatform luaPlatform = new LuaPlatform();
        LuaChunk luaChunk = luaPlatform.loadFile("return_test_object.lua");
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
    void testDeserializer() throws JsonProcessingException, URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LuaJacksonModule luaJacksonModule = new LuaJacksonModule();
        luaJacksonModule.customize(objectMapper);
        URL resource = this.getClass().getResource("/test_deserialization.json");
        String jsonString = Files.readString(Paths.get(resource.toURI()).toFile().toPath());
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
