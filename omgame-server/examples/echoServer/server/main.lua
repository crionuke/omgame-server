function tick(self, event)
    omgs.log_trace(event.id, event.tick, event.time)
end

function connected(self, event)
    omgs.log_info("Client connected, client_id=" .. event.client_id)
end

function received(self, event)
    omgs.log_debug("Data received, client_id=" .. event.client_id)
    local response = event.data
    omgs.unicast(event.client_id, response)
end

function disconnected(self, event)
    omgs.log_info("Client disconnected, client_id=" .. event.client_id)
end

print("EchoServer stared")