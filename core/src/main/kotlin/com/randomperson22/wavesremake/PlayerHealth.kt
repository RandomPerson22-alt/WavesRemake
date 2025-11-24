package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor

class PlayerHealth(
    private val player: Player,
    private val shapeRenderer: ShapeRenderer,
    private val width: Float = 60f,
    private val height: Float = 8f,
    private val offsetY: Float = 20f
) : Actor() {

    override fun act(delta: Float) {
        super.act(delta)
        // Keep health bar above player
        x = player.x + player.width / 2f - width / 2f
        y = player.y + player.height + offsetY
    }

    override fun draw(batch: com.badlogic.gdx.graphics.g2d.Batch?, parentAlpha: Float) {
        batch?.end() // End batch so we can use ShapeRenderer

        shapeRenderer.projectionMatrix = stage.camera.combined

        // Draw the outline
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()

        // Draw the background (red)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.RED
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()

        // Draw the green health portion
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GREEN
        val healthPercentage = player.health / player.maxHealth
        shapeRenderer.rect(x, y, width * healthPercentage, height)
        shapeRenderer.end()

        batch?.begin() // Resume batch for normal drawing
    }
}
