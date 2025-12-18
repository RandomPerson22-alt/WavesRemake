package com.randomperson22.wavesremake.cards

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Color

fun createCardRect(width: Int, height: Int, color: Color): Texture {
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fill()

    val texture = Texture(pixmap)
    pixmap.dispose()

    return texture
}
