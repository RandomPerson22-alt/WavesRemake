package com.randomperson22.wavesremake.cards

import com.badlogic.gdx.graphics.Color
import com.randomperson22.wavesremake.Card
import com.randomperson22.wavesremake.WaveManager
import com.randomperson22.wavesremake.AssetLoader

object SpeedUpgrade : Card(
    name = "Speed Upgrade",
    texture = createCardRect(
        width = 80,
        height = 120,   // taller than wide
        color = Color.DARK_GRAY
    ),
    onPick = Runnable {
        val player = WaveManager.player

        // Increase speed by 0.5
        player.speed *= 1.0f

        WaveManager.startNextWave()
    }
)
