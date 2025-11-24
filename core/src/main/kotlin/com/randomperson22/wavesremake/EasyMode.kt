package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
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
import com.badlogic.gdx.utils.Array as GdxArray

class EasyMode(
    private val game: Waves,
    private val loadedAssets: Map<String, Texture> // pass the loaded textures here
) : Screen {

    private lateinit var stage: Stage
    private lateinit var player: Player
    private lateinit var skin: Skin
    private lateinit var sword: Sword
    private lateinit var background: Texture
    private val shapeRenderer = ShapeRenderer()
    private lateinit var spawnPoints: Array<FloatArray>

    private lateinit var pauseButton: ImageButton
    private lateinit var pauseMenuTable: Table

    private val VIRTUAL_WIDTH = 800f
    private val VIRTUAL_HEIGHT = 500f

    private var isPaused = false

override fun show() {

    // --- Background ---
    background = loadedAssets["EasyModeBG.png"] ?: error("Texture EasyModeBG.png not found!")

    // --- Stage & Skin ---
    stage = Stage(FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT))
    val skinHandle = Gdx.files.internal("ui/uiskin.json")
    skin = Skin(skinHandle)

// --- Player ---
    player = Player(loadedAssets)
    player.setPosition(VIRTUAL_WIDTH / 2f - player.width / 2f, 50f)
    stage.addActor(player)

// --- Sword ---
    sword = Sword(player, stage, loadedAssets["sword.png"] ?: error("sword.png not loaded!"))
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
            0 -> { spawnPoints[i][0] = (0..VIRTUAL_WIDTH.toInt()).random().toFloat(); spawnPoints[i][1] = VIRTUAL_HEIGHT + offset }
            1 -> { spawnPoints[i][0] = (0..VIRTUAL_WIDTH.toInt()).random().toFloat(); spawnPoints[i][1] = -offset }
            2 -> { spawnPoints[i][0] = -offset; spawnPoints[i][1] = (0..VIRTUAL_HEIGHT.toInt()).random().toFloat() }
            3 -> { spawnPoints[i][0] = VIRTUAL_WIDTH + offset; spawnPoints[i][1] = (0..VIRTUAL_HEIGHT.toInt()).random().toFloat() }
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
        enemyCounter,
        loadedAssets
    )

    WaveManager.setupWaves(waves)

    // --- Pause Button ---
    val pauseTexture = loadedAssets["pausebutton.png"] ?: error("Texture pausebutton.png not found!")
    pauseButton = ImageButton(TextureRegionDrawable(TextureRegion(pauseTexture)))
    pauseButton.setPosition(VIRTUAL_WIDTH - pauseButton.width - 405f,
                            VIRTUAL_HEIGHT - pauseButton.height + 345f)
    pauseButton.setSize(50f, 50f)
    stage.addActor(pauseButton)
    pauseButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            isPaused = true
            pauseMenuTable.isVisible = true
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
    pauseMenuTable.add(leaveButton).pad(10f).row()
    pauseMenuTable.add(returnButton).pad(10f).row()
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
        }
    })

    // --- Name actors ---
    player.name = "player"
    sword.name = "sword"

    // --- Load into fake editor ---
    val allActors = mutableMapOf<String, Actor>()
    allActors["player"] = player
    allActors["sword"] = sword

    // --- Set input processor ---
    Gdx.input.inputProcessor = stage
}

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.2f, 0f, 1f)

        stage.batch.begin()
        stage.batch.draw(background, 0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
        stage.batch.end()

        // Draw spawn points
        shapeRenderer.projectionMatrix = stage.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 0f, 0f, 1f)
        for (i in spawnPoints.indices) {
            shapeRenderer.circle(spawnPoints[i][0], spawnPoints[i][1], 8f)
        }
        shapeRenderer.end()

        if (!isPaused) stage.act(delta)

        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
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
