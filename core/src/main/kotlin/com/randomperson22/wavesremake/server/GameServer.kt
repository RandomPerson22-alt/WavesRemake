package com.randomperson22.wavesremake.server

import RoomCreateRequest
import RoomJoinRequest
import com.esotericsoftware.kryonet.*
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class GameServer(val tcpPort: Int = 54555, val udpPort: Int = 54777) {
    private val server = Server()
    private val rooms = ConcurrentHashMap<String, Room>() // roomCode -> Room

    fun start() {
        // register shared packets
        server.kryo.apply {
            register(RoomCreateRequest::class.java)
            register(RoomCreateResponse::class.java)
            register(RoomJoinRequest::class.java)
            register(RoomJoinResponse::class.java)
            register(PlayerState::class.java)
        }

        server.start()
        server.bind(
            InetSocketAddress("0.0.0.0", tcpPort),
            InetSocketAddress("0.0.0.0", udpPort)
        )
        private val logger = org.slf4j.LoggerFactory.getLogger(GameServer::class.java)
        logger.info("Server running on TCP:$tcpPort UDP:$udpPort")

        server.addListener(object : Listener() {
            override fun received(connection: Connection, obj: Any) {
                when (obj) {
                    is RoomCreateRequest -> handleRoomCreate(connection, obj)
                    is RoomJoinRequest -> handleRoomJoin(connection, obj)
                }
            }
        })
    }

private fun generateRoomCode(): String {
    var code: String
    do {
        code = Random.nextInt(10000, 99999).toString()
    } while (rooms.containsKey(code))
    return code
}

    private fun handleRoomCreate(connection: Connection, request: RoomCreateRequest) { }

    private fun handleRoomJoin(connection: Connection, request: RoomJoinRequest) { }
}

class Room(val code: String, val hostConnection: Connection) {
    val players = ConcurrentHashMap<Int, Connection>()
    init { players[hostConnection.id] = hostConnection }
}
