package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

object AssetLoader {
    val manager = com.badlogic.gdx.assets.AssetManager()

    val texturesToLoad = listOf( // ALL NAMES ARE CASE SENSITIVE REMEMBER!!!!
        "PlayerWalk1.png",
        "PlayerWalk2.png",
        "PlayerWalk3.png",
        "PlayerWalk4.png",
        "PlayerWalk5.png",
        "PlayerWalk6.png",
        "PlayerStop1.png",
        "PlayerStop2.png",
        "EasyModeBG.png",
        "MediumModeBG.png",
        "WavesRemakeTitle.png",
        "pausebutton.png",
        "sword.png",
        "gun.png",
        "bullet.png",
        "enemy1.png",
        "enemy1Walk.png",
        "SharpnessUpgrade.png",
        "RangeUpgrade.png",
        "SpeedUpgrade.png",
        "RegenUpgrade.png"
    )

    fun loadAll() {
        for (file in texturesToLoad) {
            manager.load(file, Texture::class.java)
        }
    }
}
