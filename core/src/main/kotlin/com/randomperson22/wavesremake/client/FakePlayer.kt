// core/src/main/kotlin/com/randomperson22/wavesremake/client/FakePlayer.kt
package com.randomperson22.wavesremake.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.delay

@Serializable
data class JoinPacket(val username: String)

fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    client.webSocket(host = "127.0.0.1", port = 8080, path = "/ws") {
        println("Client connected, sending join request")

        val joinPacket = JoinPacket("Player1")
        val jsonMessage = Json.encodeToString(joinPacket)
        send(Frame.Text(jsonMessage))

        // Keep client alive for a short while to receive messages
        delay(5000)
    }

    client.close()
}
