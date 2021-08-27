local player = {}
player.__index = player

function player.create(client_id)
    local self = setmetatable({}, player)
    self.client_id = client_id
    self.last_data = nil
    omgs.log_info("Player created, client_id=" .. client_id)
    return self
end

function player:offer_data(data)
    self.last_data = data
end

function player:pull_data()
    local data = self.last_data
    self.last_data = nil
    return data
end

return player