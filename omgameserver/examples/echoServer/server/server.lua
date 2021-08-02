local server = {}

function server:tick(event)
    print(event.id, event.tick)
end

function server:client_created(event)
    print(event.id, event.client_id)
end

function server:message_received(event)
    print(event.id, event.client_id, event.message)
    local response = event.message
    runtime.send(event.client_id, response)
end

function server:client_removed(event)
    print(event.id, event.client_id)
end

runtime.add_event_listener("tick", server)
runtime.add_event_listener("client_created", server)
runtime.add_event_listener("message_received", server)
runtime.add_event_listener("client_removed", server)

print("EchoServer stared")