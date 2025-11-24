package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage

class Sword(
    private val player: Player,
    private val stage: Stage,
    private val texture: Texture  // <-- pass the preloaded texture
) : Actor() {

    private val shapeRenderer: ShapeRenderer? = if (DEBUG) ShapeRenderer() else null
    private val enemiesHitThisSwing = mutableSetOf<EnemyBase>()
    var isEquipped = false
    private var radius = 40f
    var damage = 2.5f
    private val hitboxWidth = 40f   // the actual size of the collider
    private val hitboxHeight = 16f
    private var hitBox: Polygon = Polygon(floatArrayOf(
        0f, 0f,
        hitboxWidth, 0f,
        hitboxWidth, hitboxHeight,
        0f, hitboxHeight
    ))

    companion object { private const val DEBUG = true }

    init {
        width = 40f
        height = 16f
        setOrigin(width / 2f, height / 2f)
        hitBox.setOrigin(width / 2f, height / 2f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isEquipped) return

        followMouse()
        updateHitbox()
        checkCollisions()
    }

    private fun followMouse() {
        val mousePos = stage.screenToStageCoordinates(Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()))
        val playerCenter = Vector2(player.x + player.width / 2f, player.y + player.height / 2f)
        val direction = mousePos.cpy().sub(playerCenter)

        if (direction.len2() < 1f) return
        direction.nor()

        val swordPos = playerCenter.cpy().add(direction.scl(radius))
        setPosition(swordPos.x - width / 2f, swordPos.y - height / 2f)
        rotation = direction.angleDeg()
    }

    private fun updateHitbox() {
        hitBox.setPosition(x, y)
        hitBox.rotation = rotation
    }

    private fun checkCollisions() {
        for (actor in stage.actors) {
            if (actor === this || actor === player) continue
            if (actor is EnemyBase) {
                val enemy = actor
                if (Intersector.overlapConvexPolygons(hitBox, enemy.getHitPolygon())) {
                    // Only apply damage if enemy not already hit in this swing
                    if (!enemiesHitThisSwing.contains(enemy)) {
                        enemy.takeDamage(damage)
                        enemiesHitThisSwing.add(enemy)
                    }
                } else {
                    // Sword no longer touching enemy, allow it to take damage again next touch
                    enemiesHitThisSwing.remove(enemy)
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(
            texture,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
            0, 0,
            texture.width, texture.height,
            false, false
        )

        if (shapeRenderer != null) {
            batch.end()
            shapeRenderer.projectionMatrix = stage.camera.combined
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color.RED
            shapeRenderer.polygon(hitBox.transformedVertices)
            shapeRenderer.end()
            batch.begin()
        }
    }
}
