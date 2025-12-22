// core/src/main/kotlin/com/randomperson22/wavesremake/shared/SharedPackets.kt
package com.randomperson22.wavesremake.shared

import kotlinx.serialization.Serializable

@Serializable
data class SharedPackets(
    var username: String = ""
)
