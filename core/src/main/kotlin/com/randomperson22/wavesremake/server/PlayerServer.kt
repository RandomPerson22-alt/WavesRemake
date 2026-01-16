package com.randomperson22.wavesremake.server

import com.badlogic.gdx.math.Vector2
import com.randomperson22.wavesremake.Player

class PlayerServer(id: Int) : Player(id) {
    // Apply input sent by the client (e.g., movement directions)
    fun applyInput(input: Map<String, Boolean>, delta: Float) {
        moving = false
        var dx = 0f
        var dy = 0f

        if (input["W"] == true) { dy += speed * delta; moving = true }
        if (input["S"] == true) { dy -= speed * delta; moving = true }
        if (input["A"] == true) { dx -= speed * delta; moving = true }
        if (input["D"] == true) { dx += speed * delta; moving = true }

        x += dx
        y += dy
    }

    // Server-side dash logic
    fun applyDash(dir: Vector2, distance: Float, delta: Float) {
        val moveAmount = minOf(600f * delta, distance)
        x += dir.x * moveAmount
        y += dir.y * moveAmount
        moving = true
    }
}
