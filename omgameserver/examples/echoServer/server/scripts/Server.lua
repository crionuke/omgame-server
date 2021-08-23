local Server = {}
Server.__index = Server

function Server.create()
    return setmetatable({}, Server)
end

function Server:tick(event)
    omgs.log_trace(event.id, event.tick, event.time)
end

function Server:connected(event)
    omgs.log_info("Client connected, client_id=" .. event.client_id)
end

function Server:received(event)
    omgs.log_debug("Data received, client_id=" .. event.client_id)
    local response = event.data
    omgs.unicast(event.client_id, response)
end

function Server:disconnected(event)
    omgs.log_info("Client disconnected, client_id=" .. event.client_id)
end

return Server