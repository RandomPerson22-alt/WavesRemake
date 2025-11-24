package com.randomperson22.wavesremake.cards

import com.randomperson22.wavesremake.Card
import com.randomperson22.wavesremake.WaveManager
import com.randomperson22.wavesremake.AssetLoader

object RangeUpgrade : Card(
    name = "Range Upgrade",
    texture = AssetLoader.manager.get("RangeUpgrade.png"),
    onPick = Runnable {
        val sword = WaveManager.player.sword

        // Increase width and height by 50%
        sword.setSize(sword.width * 1.5f, sword.height * 1.5f)

        WaveManager.startNextWave()
    }
)
