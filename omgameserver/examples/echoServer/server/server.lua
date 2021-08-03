local server = {}

function server:tick(event)
    print(event.id, event.tick)
end

function server:connected(event)
    print(event.id, event.client_id)
end

function server:received(event)
    print(event.id, event.client_id, event.data)
    local response = event.data
    runtime.send(event.client_id, response)
end

function server:disconnected(event)
    print(event.id, event.client_id)
end

runtime.add_event_listener("tick", server)
runtime.add_event_listener("connected", server)
runtime.add_event_listener("received", server)
runtime.add_event_listener("disconnected", server)

print("EchoServer stared")