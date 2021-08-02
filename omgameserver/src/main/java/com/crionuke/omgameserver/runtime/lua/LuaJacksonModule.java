package com.crionuke.omgameserver.runtime.lua;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import org.luaj.vm2.LuaValue;

import javax.inject.Singleton;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@Singleton
public class LuaJacksonModule implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LuaValue.class, new LuaValueSerializer(LuaValue.class));
        simpleModule.addDeserializer(LuaValue.class, new LuaValueDeserializer(LuaValue.class));
        mapper.registerModule(simpleModule);
    }
}
