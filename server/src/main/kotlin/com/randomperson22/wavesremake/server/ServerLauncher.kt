// ServerLauncher.kt
package com.randomperson22.wavesremake.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.time.Duration

@Serializable
data class JoinPacket(val username: String)

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
        }

        val clients = mutableSetOf<DefaultWebSocketServerSession>()

        routing {
            webSocket("/ws") {
                clients.add(this)
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val packet = Json.decodeFromString<JoinPacket>(text)
                            println("${packet.username} joined")

                            // Broadcast to all connected clients
                            val msg = Json.encodeToString(packet)
                            clients.forEach { it.send(msg) }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    // Client disconnected
                } finally {
                    clients.remove(this)
                }
            }
        }
    }.start(wait = true)
}
