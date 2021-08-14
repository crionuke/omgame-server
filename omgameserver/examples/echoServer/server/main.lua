local Server = require("server.scripts.Server")

local server = Server.create()

omgs.add_event_listener("tick", server)
omgs.add_event_listener("connected", server)
omgs.add_event_listener("received", server)
omgs.add_event_listener("disconnected", server)

print("EchoServer stared")