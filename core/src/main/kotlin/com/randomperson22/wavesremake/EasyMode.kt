package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.randomperson22.wavesremake.cards.RangeUpgrade
import com.randomperson22.wavesremake.cards.SharpnessUpgrade
import com.randomperson22.wavesremake.cards.SpeedUpgrade
import com.randomperson22.wavesremake.client.PlayerClient
import com.badlogic.gdx.utils.Array as GdxArray

class EasyMode(
    private val game: Waves,
) : Screen {

    private lateinit var stage: Stage
    private lateinit var player: PlayerClient
    private lateinit var skin: Skin
    private lateinit var sword: Sword
    private lateinit var background: Texture
    private val shapeRenderer = ShapeRenderer()
    private lateinit var spawnPoints: Array<FloatArray>
    private lateinit var pauseButton: ImageButton
    private lateinit var pauseMenuTable: Table
    private var vpWidth = 0f
    private var vpHeight = 0f
    private val minX get() = 0f
    private val maxX get() = stage.viewport.worldWidth
    private val minY get() = 0f
    private val maxY get() = stage.viewport.worldHeight
    private var isPaused = false
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

override fun show() {

    // --- Stage & Skin ---
    camera = OrthographicCamera()
    viewport = FitViewport(480f, 360f, camera)
    stage = Stage(viewport)
    val skinHandle = Gdx.files.internal("ui/uiskin.json")
    skin = Skin(skinHandle)

    // --- Background ---
    background = AssetLoader.manager.get("EasyModeBG.png", Texture::class.java)

    vpWidth = stage.viewport.worldWidth
    vpHeight = stage.viewport.worldHeight

// --- Player ---
    player = PlayerClient(id = 1)
    player.setPosition(vpWidth / 2f - player.width / 2f, 50f) // just position, no size
    stage.addActor(player)

// --- Sword ---
    sword = Sword(player, stage, AssetLoader.manager.get("sword.png", Texture::class.java))
    sword.isEquipped = false
    sword.isVisible = false
    stage.addActor(sword)
    player.sword = sword

    val allCards = GdxArray<Card>()
    allCards.add(SharpnessUpgrade)
    allCards.add(RangeUpgrade)
    allCards.add(SpeedUpgrade)

// --- Spawn Points ---
    spawnPoints = Array(10) { FloatArray(2) }
    val offset = 50f
    for (i in 0 until 10) {
        when (i % 4) {
            0 -> { spawnPoints[i][0] = (0..vpWidth.toInt()).random().toFloat(); spawnPoints[i][1] = vpHeight + offset }
            1 -> { spawnPoints[i][0] = (0..vpWidth.toInt()).random().toFloat(); spawnPoints[i][1] = -offset }
            2 -> { spawnPoints[i][0] = -offset; spawnPoints[i][1] = (0..vpHeight.toInt()).random().toFloat() }
            3 -> { spawnPoints[i][0] = vpWidth + offset; spawnPoints[i][1] = (0..vpHeight.toInt()).random().toFloat() }
        }
    }
    val spawnVectors = spawnPoints.map { Vector2(it[0], it[1]) }.toTypedArray()

// --- Enemy Counter ---

    val enemyCounter = EnemyCounter()

// --- Card System ---
    CardSystem.init(stage, allCards)

// Initialize the singleton WaveManager
    WaveManager.init(player, stage, skin, spawnVectors, enemyCounter)
    WaveManager.showInitialCards()
    enemyCounter.setWaveManager(WaveManager)

    val waves = EasyWaves.create(
        player,
        enemyCounter
    )

    WaveManager.setupWaves(waves)

    // --- Pause Button ---
    val pauseTexture = AssetLoader.manager.get("pausebutton.png", Texture::class.java)
    pauseButton = ImageButton(TextureRegionDrawable(TextureRegion(pauseTexture)))
    pauseButton.setPosition(
        0f + -2f, // distance from left edge
        stage.viewport.worldHeight - pauseButton.height - -365f // distance from top
    )
    pauseButton.setSize(31f, 41f)
    stage.addActor(pauseButton)
    pauseButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            isPaused = true
            pauseMenuTable.isVisible = true

            // Disable everything in the background
            WaveManager.setInitialCardsTouchable(false)
            CardSystem.setRandomPanelTouchable(false) // disable upgrade cards
        }
    })

// --- Pause Menu ---
    pauseMenuTable = Table().apply {
        setFillParent(true)
        center()
        isVisible = false
    }

    val leaveButton = TextButton("Go back To The Main Menu", skin)
    val returnButton = TextButton("Return", skin)

// Double the size of both buttons
    val buttonWidth = leaveButton.prefWidth * 2
    val buttonHeight = leaveButton.prefHeight * 2

    pauseMenuTable.add(leaveButton)
        .width(buttonWidth)
        .height(buttonHeight)
        .pad(10f)
        .row()

    pauseMenuTable.add(returnButton)
        .width(buttonWidth)
        .height(buttonHeight)
        .pad(10f)
        .row()

    stage.addActor(pauseMenuTable)


    leaveButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            game.setScreen(MainMenuScreen(game))
        }
    })
    returnButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            isPaused = false
            pauseMenuTable.isVisible = false

            // Re-enable everything
            WaveManager.setInitialCardsTouchable(true)
            CardSystem.setRandomPanelTouchable(true) // disable upgrade cards
        }
    })

    // --- Name actors ---
    player.name = "player"
    sword.name = "sword"

    // --- Set input processor ---
    Gdx.input.inputProcessor = stage
}

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        stage.batch.begin()
        stage.batch.draw(background, 0f, 0f, vpWidth, vpHeight)
        stage.batch.end()

        // Draw spawn points
        shapeRenderer.projectionMatrix = stage.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 0f, 0f, 1f)
        for (i in spawnPoints.indices) {
            shapeRenderer.circle(spawnPoints[i][0], spawnPoints[i][1], 8f)
        }
        shapeRenderer.end()

        if (!isPaused) {
            stage.act(delta)

            // Clamp player position so they can't leave the play area
            player.x = player.x.coerceIn(minX, maxX - player.width)
            player.y = player.y.coerceIn(minY, maxY - player.height)
        }

        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        shapeRenderer.dispose()
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
}
