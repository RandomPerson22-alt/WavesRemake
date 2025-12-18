package com.randomperson22.wavesremake.server

fun main() {
    // Optional: you can set custom ports here if needed
    val tcpPort = 54555
    val udpPort = 54777

    // Create the server instance
    val server = GameServer(tcpPort, udpPort)

    // Start the server
    server.start()

    println("WavesRemake server started on TCP:$tcpPort UDP:$udpPort")
}
