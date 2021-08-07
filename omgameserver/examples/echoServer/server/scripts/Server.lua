local Server = {}
Server.__index = Server

function Server.create()
    return setmetatable({}, Server)
end

function Server:tick(event)
    --print(event.id, event.tick, event.time)
end

function Server:connected(event)
    print("Client connected, client_id=" .. event.client_id)
end

function Server:received(event)
    print("Data received, client_id=" .. event.client_id)
    local response = event.data
    runtime.unicast(event.client_id, response)
end

function Server:disconnected(event)
    print("Client disconnected, client_id=" .. event.client_id)
end

return Server