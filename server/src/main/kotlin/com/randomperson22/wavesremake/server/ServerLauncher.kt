//ServerLauncher.kt

package com.randomperson22.wavesremake.server

fun main() {
    // Get port from environment or fallback to local default
    val port = System.getenv("PORT")?.toInt() ?: 54555

    // Create the server
    val server = GameServer(port, port)  // TCP and UDP ports

    try {
        // Bind to all interfaces so clients can connect externally
        server.bind(port, port, "0.0.0.0")
        server.start()
        println("WavesRemake server started on TCP:$port UDP:$port")
    } catch (e: Exception) {
        println("Failed to start server: ${e.message}")
        e.printStackTrace()
        return
    }

    // Graceful shutdown
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        println("Server stopped")
    })
}
