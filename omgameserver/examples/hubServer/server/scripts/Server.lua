local Player = require("examples.hubServer.server.scripts.Player")

local Server = {}
Server.__index = Server

function Server.create()
    self = setmetatable({}, Server)
    self.players = {}
    return self
end

function Server:tick(event)
    -- response
    local state = {
        id = "tick",
        data = {
            tick = event.tick,
            clients = {}
        }
    }
    for client_id, player in pairs(self.players) do
        state.data.clients[client_id] = player:pull_data()
    end
    -- broadcast
    for client_id, _ in pairs(self.players) do
        runtime.unicast(client_id, state)
    end
end

function Server:connected(event)
    print("Client connected, client_id=" .. event.client_id)
    local client_id = event.client_id
    self.players[client_id] = Player.create(client_id);
    runtime.unicast(client_id, {
        id = "connected",
        data = {
            client_id = client_id,
        }
    })
end

function Server:received(event)
    print("Data received, client_id=" .. event.client_id)
    local player = self.players[event.client_id]
    if player then
        player:offer_data(event.data)
    end
end

function Server:disconnected(event)
    print("Client disconnected, client_id=" .. event.client_id)
    self.players[event.client_id] = nil
end

return Server