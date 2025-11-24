package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array

object CardSystem {
    private lateinit var stage: Stage
    private lateinit var allCards: Array<Card>  // initialized in init()
    private val cardPanel = Table()
    private var panelActive = false

    fun init(stage: Stage, cards: Array<Card>) {
        this.stage = stage
        this.allCards = cards
        cardPanel.setFillParent(true)
        cardPanel.center()
        cardPanel.isVisible = false
        stage.addActor(cardPanel)
    }

    fun showRandomCardPanel() {
        cardPanel.clear()
        val deckCopy = Array(allCards)

        repeat(3) {
            if (deckCopy.size == 0) return@repeat
            val card = deckCopy.removeIndex(MathUtils.random(deckCopy.size - 1))

            val cardButton = ImageButton(TextureRegionDrawable(TextureRegion(card.texture)))
            cardButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    card.onPick.run()
                    hidePanel()
                }
            })
            cardPanel.add(cardButton).pad(10f).width(150f).height(200f)
        }

        cardPanel.row()
        cardPanel.isVisible = true
        panelActive = true
    }

    fun hidePanel() {
        cardPanel.isVisible = false
        panelActive = false
    }

    fun isPanelActive(): Boolean = panelActive
}
