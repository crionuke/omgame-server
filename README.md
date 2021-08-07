# OMGAMESERVER

Lua based server for online games.

Source code of [Luaj](https://github.com/luaj/luaj) library included to this project almost fully. Some fixes planned.
Current list of changes available here [luaj-changes.txt](https://github.com/crionuke/omgameserver/blob/main/omgameserver/luaj-changes.txt)

**List of examples - used for integration testing purpose**
- [EchoServer](https://github.com/crionuke/omgameserver/tree/main/omgameserver/examples/echoServer) - where any received data is just sent back
- [HubServer](https://github.com/crionuke/omgameserver/tree/main/omgameserver/examples/hubServer) - collects data from clients during period and sends back batch with all information by timer 
