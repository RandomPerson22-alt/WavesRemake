// core/src/main/kotlin/com/randomperson22/wavesremake/client/FakePlayer.kt
package com.randomperson22.wavesremake.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class JoinPacket(val username: String)

fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    // Connect to your Render server via WSS (port 443)
    client.webSocket(host = "wavesremake.onrender.com", port = 443, path = "/ws") {
        println("Client connected to Render server, sending join request")

        val joinPacket = JoinPacket("Player1")
        val jsonMessage = Json.encodeToString(joinPacket)
        send(Frame.Text(jsonMessage))

        // Listen for broadcasts from the server
        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    println("Received broadcast: $text")
                }
            }
        }

        // Keep client alive for 10 seconds to receive messages
        delay(10_000)
    }

    client.close()
}
