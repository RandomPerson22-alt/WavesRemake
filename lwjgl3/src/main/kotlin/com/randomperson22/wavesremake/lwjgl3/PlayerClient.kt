//WavesRemake: core/src/main/kotlin/com/randomperson22/wavesremake/client/PlayerClient.kt
package com.randomperson22.wavesremake.client

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.randomperson22.wavesremake.AssetLoader
import com.randomperson22.wavesremake.Player
import com.randomperson22.wavesremake.shared.InputPacket
import com.randomperson22.wavesremake.shared.PositionPacket
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.send
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PlayerClient(id: Int) : Player(id) {

    private val walkTextures: Array<Texture> = Array(6) { i ->
        AssetLoader.manager.get("PlayerWalk${i + 1}.png", Texture::class.java)
    }

    private val stopTextures: Array<Texture> = Array(2) { i ->
        AssetLoader.manager.get("PlayerStop${i + 1}.png", Texture::class.java)
    }

    private var walkAnimTimer = 0f
    private var idleAnimTimer = 0f
    private var walkFrame = 0
    private var idleFrame = 0
    private val walkFrameDuration = 0.1f
    private val idleFrameDuration = 0.5f
    private val dashSpeed = 300f
    private val maxDashDistance = 75f
    var controlledByServer = false
    var serverId: Int? = null
    private var wsSession: DefaultClientWebSocketSession? = null

    fun setWebSocket(session: DefaultClientWebSocketSession) {
        wsSession = session
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendInitialPosition() {
        wsSession?.let {
            GlobalScope.launch {
                it.send(Json.encodeToString(PositionPacket.serializer(), PositionPacket(x, y)))
            }
        }
    }

    override fun act(delta: Float) {
        if (controlledByServer) {
            // Send input to server
            sendInputToServer()

            // Still update local animation and movement for visual feedback
            handleDash(delta)       // optional: dash feedback
            handleMovement(delta)   // optional: local fake movement
            updateAnimation(delta)

            // But don't actually "commit" the movement as authoritative
            // The server will send the true x/y, which we should interpolate to
        } else {
            // Singleplayer mode: client is fully authoritative
            super.act(delta)
            handleDash(delta)
            handleMovement(delta)
            updateAnimation(delta)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendInputToServer() {
        if (wsSession == null || serverId == null) return
        val keys = mapOf(
            "W" to Gdx.input.isKeyPressed(Input.Keys.W),
            "A" to Gdx.input.isKeyPressed(Input.Keys.A),
            "S" to Gdx.input.isKeyPressed(Input.Keys.S),
            "D" to Gdx.input.isKeyPressed(Input.Keys.D)
        )
        val packet = InputPacket(serverId!!, keys)
        GlobalScope.launch {
            wsSession?.send(Json.encodeToString(InputPacket.serializer(), packet))
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
                dash = Player.DashState(dir, remaining)
            }
        }

        dash?.let {
            val moveAmount = minOf(dashSpeed * delta, it.remaining)
            moveBy(it.dir.x * moveAmount, it.dir.y * moveAmount)
            val remaining = it.remaining - moveAmount
            dash = if (remaining > 0f) Player.DashState(it.dir, remaining) else null
            moving = true
        }
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f
        var dy = 0f
        val wasMoving = moving
        moving = false // reset at start

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; moving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; moving = true }

        moveBy(dx, dy)

        // Reset timers if state changed
        if (moving != wasMoving) {
            if (moving) {
                idleAnimTimer = 0f
                idleFrame = 0
            } else {
                walkAnimTimer = 0f
                walkFrame = 0
            }
        }
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

    override fun draw(batch: Batch, parentAlpha: Float) {
        val currentFrame = if (moving) walkTextures[walkFrame] else stopTextures[idleFrame]
        batch.draw(currentFrame, x, y, width, height)
    }
}
