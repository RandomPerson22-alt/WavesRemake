// FakePlayerClient.kt
package com.randomperson22.wavesremake.client

import RoomCreateRequest
import RoomCreateResponse
import RoomJoinRequest
import RoomJoinResponse
import PlayerState
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener

fun main() {
    val host = "wavesremake.onrender.com"  // your Render server URL
    val tcpPort = 54555
    val udpPort = 54777

    val client = Client()
    client.kryo.apply {
        register(RoomCreateRequest::class.java)
        register(RoomCreateResponse::class.java)
        register(RoomJoinRequest::class.java)
        register(RoomJoinResponse::class.java)
        register(PlayerState::class.java)
    }

    client.start()

    try {
        println("Connecting to server $host...")
        client.connect(5000, host, tcpPort, udpPort)
        println("Connected!")
    } catch (e: Exception) {
        println("Failed to connect: ${e.message}")
        return
    }

    client.addListener(object : Listener() {
        override fun received(connection: Connection, obj: Any) {
            when (obj) {
                is RoomCreateResponse -> println("Room created: ${obj.roomCode}, host: ${obj.hostId}")
                is RoomJoinResponse -> println("Join status: ${obj.success}, message: ${obj.message}")
                is PlayerState -> println("PlayerState received: $obj")
            }
        }

        override fun disconnected(connection: Connection) {
            println("Disconnected from server")
        }
    })

    // Send a fake room creation request as “Player1”
    client.sendTCP(RoomCreateRequest("Player1"))

    // Keep the client alive to receive server messages
    Thread.sleep(20000) // 20 seconds for testing
    client.stop()
    println("Client stopped")
}
