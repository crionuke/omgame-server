package com.crionuke.omgameserver.runtime.lua.events;

public class LuaClientCreatedEvent extends LuaEvent {
    static public final String CLIENT_CREATED_EVENT_ID = "client_created";

    public LuaClientCreatedEvent(long clientId) {
        super(CLIENT_CREATED_EVENT_ID);
        set("client_id", clientId);
    }
}
