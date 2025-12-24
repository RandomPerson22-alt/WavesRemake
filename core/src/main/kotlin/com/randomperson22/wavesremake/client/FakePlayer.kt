// core/src/main/kotlin/com/randomperson22/wavesremake/client/FakePlayer.kt
package com.randomperson22.wavesremake.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class JoinPacket(val username: String)

fun main() = runBlocking {
    val client = HttpClient(CIO) { install(WebSockets) }

    client.webSocket("wss://wavesremake.onrender.com/ws") {
        println("FakePlayer connected to server")
        val joinPacket = JoinPacket("PlayerTest")
        send(Frame.Text(Json.encodeToString(joinPacket)))

        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) println("Received: ${frame.readText()}")
            }
        }

        delay(10_000)
    }

    client.close()
}
