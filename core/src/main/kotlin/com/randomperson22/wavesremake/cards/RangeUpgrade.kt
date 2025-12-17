package com.randomperson22.wavesremake.cards

import com.badlogic.gdx.graphics.Color
import com.randomperson22.wavesremake.Card
import com.randomperson22.wavesremake.WaveManager

object RangeUpgrade : Card(
    name = "Range Upgrade",
    texture = createCardRect(
        width = 120,
        height = 200,   // taller than wide
        color = Color.DARK_GRAY
    ),
    onPick = Runnable {
        val sword = WaveManager.player.sword
        // === Make the sword 1.5x bigger here ===
        WaveManager.startNextWave()
    }
)
