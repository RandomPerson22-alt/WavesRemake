package com.randomperson22.wavesremake.client

class GameClient(val host: String, val tcpPort: Int, val udpPort: Int) {
    private val client = Client()
    private val players = mutableMapOf<Int, PlayerClient>() // other players + self

    init {
        client.start()
        client.connect(5000, host, tcpPort, udpPort)
        client.addListener(object : Listener() {
            override fun received(connection: Connection, obj: Any) {
                if (obj is PlayerState) {
                    // update or create a PlayerClient for this state
                    players[obj.id]?.state = obj
                }
            }
        })
    }

    fun update(delta: Float) {
        // send player input to server
        players[myId]?.let { it.sendInput(currentInput) }
    }

    fun render(batch: SpriteBatch) {
        // render all players.
        players.values.forEach { it.render(batch) }
    }
}
