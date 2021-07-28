package com.crionuke.omgameserver.runtime.lua.events;

public class LuaMessageReceivedEvent extends LuaEvent {
    static public final String MESSAGE_RECEIVED_EVENT_ID = "message_received";

    public LuaMessageReceivedEvent(long clientId, String message) {
        super(MESSAGE_RECEIVED_EVENT_ID);
        set("client_id", clientId);
        set("message", message);
    }
}
