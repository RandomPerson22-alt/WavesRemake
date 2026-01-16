package com.randomperson22.wavesremake.cards

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.randomperson22.wavesremake.AssetLoader
import com.randomperson22.wavesremake.WaveManager
import com.randomperson22.wavesremake.Card

object SharpnessUpgrade : Card(
    name = "Sharpness Upgrade",
    texture = createCardRect(
        width = 80,
        height = 120,   // taller than wide
        color = Color.GRAY
    ),
    onPick = Runnable {
        val sword = WaveManager.player.sword
        sword?.damage += 2.5f
        Gdx.app.log("Card", "Sword damage increased to ${sword?.damage}")
        WaveManager.startNextWave()
    }
)
