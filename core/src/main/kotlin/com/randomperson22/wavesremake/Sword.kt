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
    private val player: PlayerClient
) : Actor() {

    private val texture = AssetLoader.manager.get("sword.png", Texture::class.java)
    private val shapeRenderer: ShapeRenderer? = if (DEBUG) ShapeRenderer() else null
    private val enemiesHitThisSwing = mutableSetOf<EnemyBase>()
    var isEquipped = false
    var damage = 2.5f
    var radius: Float = 32f

    private var hitBox: Polygon

    companion object { private const val DEBUG = true }

    init {
        width = 31f
        height = 12f
        setOrigin(width / 2f, height / 2f)

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
        if (!isEquipped || stage == null) return

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
