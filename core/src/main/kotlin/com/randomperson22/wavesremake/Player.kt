package com.randomperson22.wavesremake

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

open class Player(val id: Int) : Actor() {
    var maxHealth = 100f
    var health = maxHealth
    var speed = 500f
    var hasSword = false
    var moving = false

    var sword: Sword? = null

    data class DashState(val dir: Vector2, val remaining: Float)
    var dash: DashState? = null

    fun equipSword(s: Sword) {
        sword = s
        hasSword = true
    }

    // Position helper for server/client
    override fun setPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }
}
