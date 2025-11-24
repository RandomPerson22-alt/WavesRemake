package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage

abstract class EnemyBase(
    protected val player: Player,
    protected val enemyCounter: EnemyCounter,
    protected val sprite1Texture: Texture? = null,
    protected val sprite2Texture: Texture? = null
) : Actor() {

    // Movement and combat
    abstract var moveSpeed: Float
    abstract var health: Float
    abstract var damageAmount: Float

    // Animation
    protected var sprite1: Texture? = sprite1Texture
    protected var sprite2: Texture? = sprite2Texture
    protected var switchInterval = 0.5f
    protected var spriteTimer = 0f
    protected var usingFirstSprite = true
    private val hitBox: Polygon

    // Collision box
    protected val bounds = Rectangle()

    init {
        setWidth(24f)
        setHeight(24f)

        hitBox = Polygon(
            floatArrayOf(
                0f, 0f,
                width, 0f,
                width, height,
                0f, height
            )
        )
        hitBox.setOrigin(width / 2f, height / 2f)
    }

    override fun act(delta: Float) {
        super.act(delta)

        moveTowardsPlayer(delta)
        animateSprite(delta)

        bounds.set(x, y, width, height)
    }

    protected open fun moveTowardsPlayer(delta: Float) {
        val direction = Vector2(player.x - x, player.y - y).nor()
        moveBy(direction.x * moveSpeed * delta, direction.y * moveSpeed * delta)
    }

    fun getHitPolygon(): Polygon {
        hitBox.setPosition(x, y)
        hitBox.setRotation(rotation)
        return hitBox
    }

    protected open fun animateSprite(delta: Float) {
        spriteTimer += delta
        if (spriteTimer >= switchInterval) {
            spriteTimer = 0f
            usingFirstSprite = !usingFirstSprite
        }
    }

    fun takeDamage(amount: Float) {
        health -= amount
        if (health <= 0) die()
    }

    protected open fun die() {
        enemyCounter.decreaseEnemyCount()
        remove()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val current = if (usingFirstSprite) sprite1 else sprite2
        current?.let {
            batch.draw(it, x, y, width, height)
        }
    }
}
