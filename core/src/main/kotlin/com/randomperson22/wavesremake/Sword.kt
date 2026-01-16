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
import com.randomperson22.wavesremake.client.PlayerClient

class Sword(
    private val player: PlayerClient,
    private val stage: Stage,
    private val texture: Texture
) : Actor() {

    private val shapeRenderer: ShapeRenderer? = if (DEBUG) ShapeRenderer() else null
    private val enemiesHitThisSwing = mutableSetOf<EnemyBase>()
    var isEquipped = false
    var damage = 2.5f
    var radius: Float = 32f

    // Hitbox now matches the sprite size
    private var hitBox: Polygon = Polygon(floatArrayOf(
        0f, 0f,
        width, 0f,
        width, height,
        0f, height
    ))

    companion object { private const val DEBUG = true }

    init {
        // Set your desired in-game size
        width = 31f   // how big the sword appears in the game
        height = 12f
        setOrigin(width / 2f, height / 2f)

        // Hitbox matches the displayed size
        hitBox = Polygon(floatArrayOf(
            0f, 0f,
            width, 0f,
            width, height,
            0f, height
        ))
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
                if (Intersector.overlapConvexPolygons(hitBox, actor.getHitPolygon())) {
                    if (!enemiesHitThisSwing.contains(actor)) {
                        actor.takeDamage(damage)
                        enemiesHitThisSwing.add(actor)
                    }
                } else {
                    enemiesHitThisSwing.remove(actor)
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
