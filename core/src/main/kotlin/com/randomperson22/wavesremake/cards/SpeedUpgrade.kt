package com.randomperson22.wavesremake.cards

import com.randomperson22.wavesremake.Card
import com.randomperson22.wavesremake.WaveManager
import com.randomperson22.wavesremake.AssetLoader

object SpeedUpgrade : Card(
    name = "Speed Upgrade",
    texture = AssetLoader.manager.get("SpeedUpgrade.png"),
    onPick = Runnable {
        val player = WaveManager.player

        // Increase speed by 0.5
        player.speed *= 0.5f

        WaveManager.startNextWave()
    }
)
