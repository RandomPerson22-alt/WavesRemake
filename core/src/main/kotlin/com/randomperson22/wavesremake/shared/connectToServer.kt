package com.randomperson22.wavesremake.shared

import com.randomperson22.wavesremake.client.PlayerClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

fun connectToServer(player: PlayerClient, serverUrl: String) {
    val client = HttpClient(CIO) { install(WebSockets) }

    CoroutineScope(Dispatchers.IO).launch {
        client.webSocket(serverUrl) {
            player.setWebSocket(this) // so player can send input

            // Receive init packet
            val frame = incoming.receive() as Frame.Text
            val initPacket = Json.decodeFromString(InitPlayerPacket.serializer(), frame.readText())
            player.controlledByServer = true
            player.serverId = initPacket.id
            player.setPosition(initPacket.x, initPacket.y)

            // Keep listening for server updates
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    // parse updates from server and apply to player
                }
            }
        }
    }
}
