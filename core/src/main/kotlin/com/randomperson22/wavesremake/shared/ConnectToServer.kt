package com.randomperson22.wavesremake.shared

import com.badlogic.gdx.Gdx
import com.randomperson22.wavesremake.client.PlayerClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

fun connectToServer(player: PlayerClient, serverUrl: String) {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    CoroutineScope(Dispatchers.IO).launch {
        client.webSocket(serverUrl) {
            println("✅ Connected to server")

            player.setWebSocket(this)

            // --- Wait for init packet ---
            val firstFrame = incoming.receive()
            if (firstFrame !is Frame.Text) return@webSocket

            val initPacket = Json.decodeFromString(
                InitPlayerPacket.serializer(),
                firstFrame.readText()
            )

            // Apply init on LibGDX thread
            Gdx.app.postRunnable {
                player.controlledByServer = true
                player.serverId = initPacket.id
                player.setPosition(initPacket.x, initPacket.y)
                player.sendInitialPosition()
            }

            // --- Receive loop ---
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()

                    Gdx.app.postRunnable {
                        // apply server updates here
                    }
                }
            }
        }
    }
}
