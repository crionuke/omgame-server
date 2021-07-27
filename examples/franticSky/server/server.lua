local server = {}

function server:tick(event)
    print(event.id, event.tick)
end

runtime.add_event_listener("tick", server)

print("FranticSky server stared")