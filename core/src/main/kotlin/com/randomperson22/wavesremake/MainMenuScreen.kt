package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlin.math.abs

class MainMenuScreen(private val game: Waves) : Screen {

    private lateinit var stage: Stage
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var skin: Skin
    private var loadingFinished = false
    private lateinit var loadedTextures: MutableMap<String, Texture>

    // Splash textures (manual load)
    private lateinit var titleImage: Image
    private lateinit var playerImage: Image
    private lateinit var bigBossImage: Image
    private lateinit var smallBossImage: Image
    private lateinit var smallBoss2Image: Image

    // Original stuff only for animations
    private lateinit var bigBossOriginalPos: Vector2
    private lateinit var smallBossOriginalPos: Vector2
    private lateinit var smallBoss2OriginalPos: Vector2

    private lateinit var clickSound: Sound

    // UI buttons fade in later
    private lateinit var buttonTable: Table
    private lateinit var quitButton: TextButton
    private var buttonsShown = false

    override fun show() {
        stage = Stage(ScreenViewport())
        shapeRenderer = ShapeRenderer()
        skin = Skin(Gdx.files.internal("ui/uiskin.json"))
        Gdx.input.inputProcessor = stage

        val vpWidth = stage.viewport.worldWidth
        val vpHeight = stage.viewport.worldHeight

        // --- Load splash assets manually ---
        val titleTex = Texture("WavesRemakeTitle.png")
        val playerTex = Texture("playerbackground.png")
        val bigBossTex = Texture("bossbackground.png")
        val easyBossTex = Texture("EASYBOSS.png")
        clickSound = Gdx.audio.newSound(Gdx.files.internal("ClickSound.mp3"))

        titleImage = Image(titleTex).apply {
            setSize(920f, 800f)
            setPosition(vpWidth / 2f - width / 2f, vpHeight - height - 150f)
        }
        stage.addActor(titleImage)

// Inside show() or initialization
        playerImage = Image(playerTex).apply {
            setSize(800f, 600f)
            setPosition(vpWidth / 2f - 800f / 2f, vpHeight - 600f - 430f) // regular position
            color.a = 0f
        }
        stage.addActor(playerImage)

        bigBossOriginalPos = Vector2(vpWidth / 2f - 800f/1.87f, vpHeight * 0.62f - 200f)
        bigBossImage = Image(bigBossTex).apply {
            setSize(800f, 600f)
            setPosition(bigBossOriginalPos.x, bigBossOriginalPos.y - 1000f)
        }
        stage.addActor(bigBossImage)

        smallBossOriginalPos = Vector2(vpWidth * 0.05f, vpHeight * 0.75f - 200f)
        smallBossImage = Image(easyBossTex).apply {
            setSize(130f, 260f)
            setPosition(smallBossOriginalPos.x, smallBossOriginalPos.y - 950f)
        }
        stage.addActor(smallBossImage)

        smallBoss2OriginalPos = Vector2(vpWidth * 0.87f, vpHeight * 0.75f - 200f)
        smallBoss2Image = Image(easyBossTex).apply {
            setSize(130f, 260f)
            setPosition(smallBoss2OriginalPos.x, smallBoss2OriginalPos.y - 950f)
        }
        stage.addActor(smallBoss2Image)

        // --- Start loading everything else through AssetManager ---
        AssetLoader.loadAll()

        // --- Create menu buttons but hide them ---
        createButtons(vpWidth, vpHeight)

        playerImage.toFront() // bring player in front of everything
        bigBossImage.toBack() // push big boss behind everything
    }

    fun moveToWithSpeed(actor: Image, targetX: Float, targetY: Float, speed: Float) {
        val distance = abs(targetY - actor.y)
        val duration = distance / speed
        actor.addAction(Actions.moveTo(targetX, targetY, duration))
    }

    private fun createButtons(vpWidth: Float, vpHeight: Float) {
        val buttonWidth = 300f
        val buttonHeight = 70f

        val easy = TextButton("Easy", skin)
        val med = TextButton("Medium", skin)
        val hard = TextButton("Hard", skin)

        listOf(easy, med, hard).forEach {
            it.label.setFontScale(3f)
            it.label.color = Color.WHITE
        }

        buttonTable = Table().apply {
            bottom().left().padLeft(30f).padBottom(35f)
            add(easy).width(buttonWidth).height(buttonHeight).padBottom(20f).row()
            add(med).width(buttonWidth).height(buttonHeight).padBottom(20f).row()
            add(hard).width(buttonWidth).height(buttonHeight)
            isVisible = false
        }

        stage.addActor(buttonTable)

        quitButton = TextButton("Quit", skin).apply {
            label.setFontScale(3f)
            label.color = Color.WHITE
            setSize(buttonWidth, buttonHeight)
            setPosition(vpWidth * 0.76f, vpHeight * 0.13f)
            isVisible = false
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
        }
        stage.addActor(quitButton)

        easy.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                clickSound.play()
                game.setScreen(EasyMode(game, loadedTextures))
            }
        })
    }

    override fun render(delta: Float) {
        // Clear the screen
        ScreenUtils.clear(0f, 0.2f, 0f, 1f)

        // Update the AssetManager; returns false until everything is loaded
        if (!AssetLoader.manager.update()) {
            // Still loading → show the progress bar
            drawProgressBar(AssetLoader.manager.progress)
        } else if (!loadingFinished) {
            // First frame after loading finishes
            loadingFinished = true

            // Grab all textures from AssetManager into a map
            loadedTextures = hashMapOf()
            for (file in AssetLoader.texturesToLoad) {
                loadedTextures[file] = AssetLoader.manager.get(file, Texture::class.java)
            }

            // Start any animations or transitions
            startAnimations()
        }

        // Update and draw the stage/UI
        stage.act(delta)
        stage.draw()
    }

    private fun drawProgressBar(progress: Float) {
        val barWidth = 400f
        val barHeight = 30f
        val barX = stage.viewport.worldWidth / 2f - barWidth / 2f
        val barY = stage.viewport.worldHeight * 0.1f

        shapeRenderer.projectionMatrix = stage.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(barX, barY, barWidth, barHeight)

        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight)

        shapeRenderer.end()
    }

    private fun startAnimations() {
        playerImage.addAction(
            Actions.sequence(
                Actions.fadeIn(1f),
                Actions.run { animateBigBoss() }
            )
        )
    }

    private fun animateBigBoss() {
        val speed = 1000f
        val distance = abs(bigBossOriginalPos.y - bigBossImage.y)
        val duration = distance / speed

        bigBossImage.addAction(
            Actions.sequence(
                Actions.moveTo(bigBossOriginalPos.x, bigBossOriginalPos.y, duration),
                Actions.run { animateSmallBosses() } // now called after big boss finishes
            )
        )
    }

    private fun animateSmallBosses() {
        val speed = 1000f

        smallBossImage.addAction(
            Actions.sequence(
                Actions.delay(0.4f),
                Actions.run {
                    moveToWithSpeed(smallBossImage, smallBossOriginalPos.x, smallBossOriginalPos.y, speed)
                }
            )
        )

        smallBoss2Image.addAction(
            Actions.sequence(
                Actions.delay(0.6f),
                Actions.run {
                    moveToWithSpeed(smallBoss2Image, smallBoss2OriginalPos.x, smallBoss2OriginalPos.y, speed)
                },
                Actions.run { showButtons() } // will run after smallBoss2 starts moving
            )
        )
    }

    private fun showButtons() {
        if (buttonsShown) return
        buttonsShown = true

        buttonTable.isVisible = true
        quitButton.isVisible = true

        buttonTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        quitButton.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
    }

    override fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
        skin.dispose()
        clickSound.dispose()
    }

    override fun resize(width: Int, height: Int) {}
    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
}
