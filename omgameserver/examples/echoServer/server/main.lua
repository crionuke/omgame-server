local Server = require("server.scripts.Server")

local server = Server.create()

runtime.add_event_listener("tick", server)
runtime.add_event_listener("connected", server)
runtime.add_event_listener("received", server)
runtime.add_event_listener("disconnected", server)

print("EchoServer stared")