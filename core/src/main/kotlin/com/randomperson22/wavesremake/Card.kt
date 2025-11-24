package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

open class Card(
    val name: String,
    val texture: Texture,
    val onPick: Runnable
)
