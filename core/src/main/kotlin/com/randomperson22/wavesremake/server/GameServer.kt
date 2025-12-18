package com.randomperson22.wavesremake.server

class GameServer(val tcpPort: Int, val udpPort: Int) {
    private val server = Server()
    private val players = mutableMapOf<Int, PlayerServer>()

    init {
        // start KryoNet server
        server.start()
        server.bind(tcpPort, udpPort)
        server.addListener(object : Listener() {
            override fun received(connection: Connection, obj: Any) {
                // handle incoming PlayerInput packets
                if (obj is PlayerInput) {
                    players[connection.id]?.applyInput(obj)
                }
            }

            override fun connected(connection: Connection) {
                players[connection.id] = PlayerServer(connection.id, PlayerState(0f,0f,100))
            }

            override fun disconnected(connection: Connection) {
                players.remove(connection.id)
            }
        })
    }

    fun update(delta: Float) {
        // update all player states
        players.values.forEach { it.update(delta) }

        // broadcast all player states to clients
        players.values.forEach { server.sendToAllUDP(it.state) }
    }
}
