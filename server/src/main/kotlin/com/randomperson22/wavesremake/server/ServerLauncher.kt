// server/src/main/kotlin/com/randomperson22/wavesremake/server/ServerLauncher.kt
package com.randomperson22.wavesremake.server

import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.time.Duration

@Serializable
data class JoinPacket(val username: String)

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val clients = mutableSetOf<DefaultWebSocketServerSession>()
    val players = mutableMapOf<DefaultWebSocketServerSession, PlayerServer>()

    embeddedServer(Netty, port = port) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
        }

        routing {
            webSocket("/ws") {
                clients.add(this)
                val player = PlayerServer(clients.size)
                players[this] = player
                println("New client connected, total clients: ${clients.size}")

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val packet = Json.decodeFromString<JoinPacket>(text)
                            println("${packet.username} joined (server player id=${player.id})")

                            // Broadcast to all clients
                            val msg = Json.encodeToString(packet)
                            clients.forEach { it.send(msg) }
                        }
                    }
                } catch (_: ClosedReceiveChannelException) {
                } finally {
                    clients.remove(this)
                    players.remove(this)
                    println("Client disconnected, total clients: ${clients.size}")
                }
            }
        }
    }.start(wait = true)
}
