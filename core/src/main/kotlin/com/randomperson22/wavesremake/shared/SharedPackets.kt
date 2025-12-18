// SharedPackets.kt

// Room management
data class RoomCreateRequest(val playerName: String)
data class RoomCreateResponse(val roomCode: String, val hostId: Int)
data class RoomJoinRequest(val roomCode: String, val playerName: String)
data class RoomJoinResponse(val success: Boolean, val message: String, val hostId: Int? = null)

// Player state syncing
data class PlayerState(val id: Int, var x: Float, var y: Float)

// Enemy state syncing
data class EnemyState(val id: Int, var x: Float, var y: Float, var health: Float, var alive: Boolean = true)

// Player attack
data class PlayerAttack(val playerId: Int, val enemyId: Int, val damage: Float)
