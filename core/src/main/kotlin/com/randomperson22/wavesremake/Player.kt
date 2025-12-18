package com.randomperson22.wavesremake

import com.badlogic.gdx.math.Vector2

open class PlayerBase(
    val id: Int,
    var x: Float = 100f,
    var y: Float = 100f,
    var width: Float = 40f,
    var height: Float = 43f
) {
    var maxHealth = 100f
    var health = maxHealth
    var speed = 500f

    var hasSword = false
    var moving = false

    // Dash state for server logic if needed
    data class DashState(val dir: Vector2, val remaining: Float)
    var dash: DashState? = null

    // Position helper for server/client
    fun setPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }
}
