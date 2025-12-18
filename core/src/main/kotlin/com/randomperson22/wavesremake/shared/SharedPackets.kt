// SharedPackets.kt

// A packet for creating a room
data class RoomCreateRequest(val playerName: String)

// Server reply with room code
data class RoomCreateResponse(val roomCode: String, val hostId: Int)

// Packet for joining a room
data class RoomJoinRequest(val roomCode: String, val playerName: String)

// Server reply for join attempt
data class RoomJoinResponse(
    val success: Boolean,
    val message: String,
    val hostId: Int? = null
)

// Example of syncing player position
data class PlayerState(val id: Int, var x: Float, var y: Float)
