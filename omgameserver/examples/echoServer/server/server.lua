local server = {}

function server:tick(event)
    print(event.id, event.tick)
end

function server:message_received(event)
    print(event.id, event.client_id, event.message)
end

runtime.add_event_listener("tick", server)
runtime.add_event_listener("message_received", server)

print("EchoServer stared")