package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class TutorialScreen(val game: Waves) : Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    private val label = Label("", skin)
    private val nextButton = TextButton("Next", skin)
    private val skipButton = TextButton("Skip", skin)

    private val steps = listOf(
        "Welcome to the game! Use arrows to move.",
        "Collect coins to earn points.",
        "Avoid enemies or you'll lose health.",
        "Good luck!"
    )
    private var currentStep = 0

    init {
        label.setWrap(true)
        label.setSize(Gdx.graphics.width * 0.8f, Gdx.graphics.height * 0.5f)
        label.setPosition(Gdx.graphics.width * 0.1f, Gdx.graphics.height * 0.4f)
        label.setAlignment(Align.center)

        nextButton.setSize(150f, 60f)
        nextButton.setPosition(Gdx.graphics.width - 170f, 50f)
        nextButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                currentStep++
                if (currentStep >= steps.size) {
                    game.screen = MainMenuScreen(game)// or your game screen
                } else {
                    label.setText(steps[currentStep])
                }
            }
        })

        skipButton.setSize(150f, 60f)
        skipButton.setPosition(20f, 50f)
        skipButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game) // skip tutorial
            }
        })

        stage.addActor(label)
        stage.addActor(nextButton)
        stage.addActor(skipButton)

        label.setText(steps[currentStep])
        Gdx.input.inputProcessor = stage
    }

    override fun show() {}
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() { stage.dispose() }
}
