//ServerLauncher.kt

package com.randomperson22.wavesremake.server

import com.esotericsoftware.kryonet.Server
import com.randomperson22.wavesremake.shared.SharedPackets

fun main() {
    val server = Server()
    server.start()

    // Bind TCP + UDP ports
    val tcpPort = 54555
    val udpPort = 54777
    server.bind(tcpPort, udpPort)
    println("Server running on TCP:$tcpPort / UDP:$udpPort")

    // ✅ Register the packet class
    server.kryo.register(SharedPackets::class.java)

    server.addListener(object : com.esotericsoftware.kryonet.Listener() {
        override fun received(connection: com.esotericsoftware.kryonet.Connection, obj: Any) {
            if (obj is SharedPackets) {
                println("${obj.username} joined")
            }
        }
    })

    Thread.currentThread().join()
}
