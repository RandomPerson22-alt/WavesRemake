package com.randomperson22.wavesremake.client

class GameClient(val hostAddress: String, val tcpPort: Int = 54555, val udpPort: Int = 54777) {
    private val client = Client()

    init {
        client.kryo.apply {
            register(RoomCreateRequest::class.java)
            register(RoomCreateResponse::class.java)
            register(RoomJoinRequest::class.java)
            register(RoomJoinResponse::class.java)
            register(PlayerState::class.java)
        }

        client.start()
        client.connect(5000, hostAddress, tcpPort, udpPort)

        client.addListener(object : Listener() {
            override fun received(connection: Connection, obj: Any) {
                when(obj) {
                    is RoomCreateResponse -> println("Room created: ${obj.roomCode}, you are host ${obj.hostId}")
                    is RoomJoinResponse -> println("Join status: ${obj.success}, message: ${obj.message}")
                }
            }
        })
    }

    fun createRoom(playerName: String) {
        client.sendTCP(RoomCreateRequest(playerName))
    }

    fun joinRoom(roomCode: String, playerName: String) {
        client.sendTCP(RoomJoinRequest(roomCode, playerName))
    }
}
