local Player = {}
Player.__index = Player

function Player.create(client_id)
    local self = setmetatable({}, Player)
    self.client_id = client_id
    self.last_data = nil
    omgs.log_info("Player created, client_id=" .. client_id)
    return self
end

function Player:offer_data(data)
    self.last_data = data
end

function Player:pull_data()
    local data = self.last_data
    self.last_data = nil
    return data
end

return Player