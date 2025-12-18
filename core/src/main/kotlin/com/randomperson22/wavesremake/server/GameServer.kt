package com.randomperson22.wavesremake.server

import com.esotericsoftware.kryonet.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class GameServer(val tcpPort: Int = 54555, val udpPort: Int = 54777) {
    private val server = Server()
    private val rooms = ConcurrentHashMap<String, Room>() // roomCode -> Room

    init {
        // register shared packets
        server.kryo.apply {
            register(RoomCreateRequest::class.java)
            register(RoomCreateResponse::class.java)
            register(RoomJoinRequest::class.java)
            register(RoomJoinResponse::class.java)
            register(PlayerState::class.java)
        }

        server.start()
        server.bind(tcpPort, udpPort)
        println("Server running on TCP:$tcpPort UDP:$udpPort")

        server.addListener(object : Listener() {
            override fun received(connection: Connection, obj: Any) {
                when(obj) {
                    is RoomCreateRequest -> handleRoomCreate(connection, obj)
                    is RoomJoinRequest -> handleRoomJoin(connection, obj)
                }
            }
        })
    }

    private fun generateRoomCode(): String {
        return Random.nextInt(10000, 99999).toString() // random 5-digit code
    }

    private fun handleRoomCreate(connection: Connection, request: RoomCreateRequest) {
        val code = generateRoomCode()
        val room = Room(code, connection)
        rooms[code] = room
        println("Room created: $code by ${request.playerName}")
        connection.sendTCP(RoomCreateResponse(code, connection.id))
    }

    private fun handleRoomJoin(connection: Connection, request: RoomJoinRequest) {
        val room = rooms[request.roomCode]
        if (room != null) {
            room.players[connection.id] = connection
            println("${request.playerName} joined room ${request.roomCode}")
            connection.sendTCP(RoomJoinResponse(true, "Joined room successfully", room.hostConnection.id))
            // Notify host
            room.hostConnection.sendTCP(RoomJoinResponse(true, "${request.playerName} joined your room"))
        } else {
            connection.sendTCP(RoomJoinResponse(false, "Room not found"))
        }
    }
}

// Room class to manage a single room
class Room(val code: String, val hostConnection: Connection) {
    val players = ConcurrentHashMap<Int, Connection>() // includes host
    init { players[hostConnection.id] = hostConnection }
}
