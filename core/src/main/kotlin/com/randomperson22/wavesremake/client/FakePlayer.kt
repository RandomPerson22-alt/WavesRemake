// core/src/main/kotlin/com/randomperson22/wavesremake/client/FakePlayer.kt
package com.randomperson22.wavesremake.client

import com.esotericsoftware.kryonet.Client
import com.randomperson22.wavesremake.shared.SharedPackets
import kotlin.concurrent.thread
import java.util.concurrent.TimeUnit

fun main() {
    val client = Client()
    client.start()

    // Register the packet class (must match server)
    client.kryo.register(SharedPackets::class.java)

    thread {
        client.connect(5000, "127.0.0.1", 54555, 54777)
        println("Client connected, sending join request")

        client.sendTCP(SharedPackets("Player1"))

        // Keep client alive for a short while
        TimeUnit.SECONDS.sleep(5)
        client.stop()
    }
}
