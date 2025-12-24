// server/src/main/kotlin/com/randomperson22/wavesremake/server/ServerLauncher.kt
package com.randomperson22.wavesremake.server

import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.time.Duration
import com.randomperson22.wavesremake.shared.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val clients = mutableSetOf<DefaultWebSocketServerSession>()
    val players = mutableMapOf<DefaultWebSocketServerSession, PlayerServer>()
    var nextId = 1

    embeddedServer(Netty, port = port) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
        }

        routing {
            webSocket("/ws") {
                clients.add(this)
                val player = PlayerServer(nextId++)
                players[this] = player

                // Send init packet to client
                val initPacket = InitPlayerPacket(player.id, player.x, player.y)
                send(Json.encodeToString(InitPlayerPacket.serializer(), initPacket))

                println("New client connected, total clients: ${clients.size}")

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            // Try to parse InputPacket
                            try {
                                val inputPacket = Json.decodeFromString(InputPacket.serializer(), text)
                                players[this]?.applyInput(inputPacket.keys, 1/60f) // example delta
                            } catch (_: Exception) {
                                // Could be other packet type (JoinPacket etc.)
                            }

                            // Broadcast all server players positions
                            val states = players.values.map { InitPlayerPacket(it.id, it.x, it.y) }
                            val msg = Json.encodeToString(states)
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
