//SharedPackets.kt
package com.randomperson22.wavesremake.shared

import kotlinx.serialization.Serializable

@Serializable
data class InitPlayerPacket(
    val id: Int,
    val x: Float,
    val y: Float
)

@Serializable
data class InputPacket(
    val id: Int,
    val keys: Map<String, Boolean>
)

@Serializable
data class JoinPacket(
    val username: String
)
