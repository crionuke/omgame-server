local player_mod = require("server.player")

function init(self, event)
    self.players = {}
    self.countPlayers = 0
end

function tick(self, event)
    if self.countPlayers > 0 then
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
        omgs.broadcast(state)
    end
end

function connected(self, event)
    omgs.log_info("Client connected, client_id=" .. event.client_id)
    local client_id = event.client_id
    self.players[client_id] = player_mod.create(client_id);
    self.countPlayers = self.countPlayers + 1
    omgs.unicast(client_id, {
        id = "connected",
        data = {
            client_id = client_id,
        }
    })
end

function received(self, event)
    omgs.log_debug("Data received, client_id=" .. event.client_id)
    local player = self.players[event.client_id]
    if player then
        player:offer_data(event.data)
    end
end

function disconnected(self, event)
    omgs.log_info("Client disconnected, client_id=" .. event.client_id)
    self.players[event.client_id] = nil
    self.countPlayers = self.countPlayers - 1
end

print("HubServer stared")