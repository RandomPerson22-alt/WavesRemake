package com.randomperson22.wavesremake

import com.badlogic.gdx.Game

class Waves : Game() {
    override fun create() {
        setScreen(MainMenuScreen(this))
    }

    object NetworkState {
        var connected = false
        var roomCode: String? = null
    }
}
