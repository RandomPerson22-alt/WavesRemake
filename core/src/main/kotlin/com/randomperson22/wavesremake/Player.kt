package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

class Player(
    loadedAssets: Map<String, Texture>
) : Actor() {
    var maxHealth = 100f
    var health = maxHealth

    private val walkTextures: Array<Texture> = Array(6) { i ->
        loadedAssets["PlayerWalk${i + 1}.png"] ?: error("PlayerWalk${i + 1}.png not loaded!")
    }

    private val stopTextures: Array<Texture> = Array(2) { i ->
        loadedAssets["PlayerStop${i + 1}.png"] ?: error("PlayerStop${i + 1}.png not loaded!")
    }

    private var walkAnimTimer = 0f
    private var idleAnimTimer = 0f

    private var walkFrame = 0
    private var idleFrame = 0
    private var moving = false
    private var hasSword = false
    var speed = 500f // default movement speed

    private data class DashState(val dir: Vector2, val remaining: Float)

    private var dash: DashState? = null
    lateinit var sword: Sword
    private val walkFrameDuration = 0.1f
    private val idleFrameDuration = 0.5f
    private val dashSpeed = 600f
    private val maxDashDistance = 150f

    init {
        width = 40f
        height = 43f
        setPosition(100f, 100f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        moving = false

        handleDash(delta)
        if (dash == null) {
            handleMovement(delta)
            updateAnimation(delta)
        }
    }

    private fun handleDash(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && dash == null) {
            val mousePos = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            stage.screenToStageCoordinates(mousePos)

            val dir = Vector2(mousePos.x - x, mousePos.y - y)
            val remaining = minOf(dir.len(), maxDashDistance)

            if (remaining > 1f) {
                dir.nor()
                dash = DashState(dir, remaining)
            }
        }

        dash?.let {
            val moveAmount = minOf(dashSpeed * delta, it.remaining)
            moveBy(it.dir.x * moveAmount, it.dir.y * moveAmount)
            val remaining = it.remaining - moveAmount
            dash = if (remaining > 0f) DashState(it.dir, remaining) else null
            moving = true
        }
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f
        var dy = 0f

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; moving = true }

        moveBy(dx, dy)
    }

    private fun updateAnimation(delta: Float) {
        if (moving) {
            walkAnimTimer += delta
            if (walkAnimTimer >= walkFrameDuration) {
                walkAnimTimer = 0f
                walkFrame = (walkFrame + 1) % walkTextures.size
            }
        } else {
            idleAnimTimer += delta
            if (idleAnimTimer >= idleFrameDuration) {
                idleAnimTimer = 0f
                idleFrame = (idleFrame + 1) % stopTextures.size
            }
        }
    }

    fun equipSword() {
        hasSword = true
        sword.isVisible = true
        sword.isEquipped = true
        println("Player equipped the sword!")
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val currentFrame = if (moving) walkTextures[walkFrame] else stopTextures[idleFrame]
        batch.draw(currentFrame, x, y, width, height)
    }
}
