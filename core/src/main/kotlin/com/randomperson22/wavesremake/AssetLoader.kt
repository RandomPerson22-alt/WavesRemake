package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

object AssetLoader {
    val manager = com.badlogic.gdx.assets.AssetManager()

    fun loadAll() {
        manager.load("PlayerWalk1.png", Texture::class.java)
        manager.load("PlayerWalk2.png", Texture::class.java)
        manager.load("PlayerWalk3.png", Texture::class.java)
        manager.load("PlayerWalk4.png", Texture::class.java)
        manager.load("PlayerWalk5.png", Texture::class.java)
        manager.load("PlayerWalk6.png", Texture::class.java)
        manager.load("PlayerStop1.png", Texture::class.java)
        manager.load("PlayerStop2.png", Texture::class.java)

        manager.load("EasyModeBG.png", Texture::class.java)
        manager.load("WavesRemakeTitle.png", Texture::class.java)
        manager.load("pausebutton.png", Texture::class.java)

        manager.load("sword.png", Texture::class.java)
        manager.load("enemy1.png", Texture::class.java)
        manager.load("enemy1Walk.png", Texture::class.java)

        manager.load("SharpnessUpgrade.png", Texture::class.java)
        manager.load("RangeUpgrade.png", Texture::class.java)
        manager.load("SpeedUpgrade.png", Texture::class.java)
        manager.load("RegenUpgrade.png", Texture::class.java)

        // Sounds except ClickSound.mp3
        // manager.load("SomeOtherSound.mp3", Sound::class.java)
    }

    fun dispose() {
        manager.dispose()
    }
}
