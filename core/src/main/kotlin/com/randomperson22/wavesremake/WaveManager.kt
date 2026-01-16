package com.randomperson22.wavesremake

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

object WaveManager {
    lateinit var player: Player
    lateinit var stage: Stage
    lateinit var skin: Skin
    lateinit var spawnPoints: Array<Vector2>
    lateinit var enemyCounter: EnemyCounter
    var spawnDelay: Float = 0.5f

    private val waves = mutableListOf<Wave>()
    private var currentWaveIndex = 0
    private lateinit var initialCardPanel: Table

    data class EnemySpawnData(val factory: () -> EnemyBase, val count: Int)
    data class Wave(val waveName: String, val enemies: List<EnemySpawnData>)

    fun init(
        player: Player,
        stage: Stage,
        skin: Skin,
        spawnPoints: Array<Vector2>,
        enemyCounter: EnemyCounter,
        spawnDelay: Float = 0.5f
    ) {
        this.player = player
        this.stage = stage
        this.skin = skin
        this.spawnPoints = spawnPoints
        this.enemyCounter = enemyCounter
        this.spawnDelay = spawnDelay
    }

    fun showInitialCards() {
        initialCardPanel = Table().apply {
            setFillParent(true)
            center()
        }

        val swordButton = TextButton("Sword", skin)

        initialCardPanel.add(swordButton).pad(20f).width(150f).height(50f)
        initialCardPanel.row()

        swordButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                selectInitialWeapon("Sword")
            }
        })

        stage.addActor(initialCardPanel)
    }

    fun setInitialCardsTouchable(enabled: Boolean) {
        if (!::initialCardPanel.isInitialized) return
        initialCardPanel.children.forEach { it.touchable =
            if (enabled) com.badlogic.gdx.scenes.scene2d.Touchable.enabled
            else com.badlogic.gdx.scenes.scene2d.Touchable.disabled
        }
    }

    private fun selectInitialWeapon(weapon: String) {
        initialCardPanel.isVisible = false
        when (weapon) {
            "Sword" -> {
                player.equipSword(player.sword!!) // pass the existing sword
                player.sword!!.isEquipped = true
                player.sword!!.isVisible = true
            }
        }
        startNextWave()
    }

    fun startNextWave() {
        if (currentWaveIndex >= waves.size) return
        enemyCounter.resetForNextWave()
        val wave = waves[currentWaveIndex]
        currentWaveIndex++
        spawnWave(wave)
    }

    private fun spawnWave(wave: Wave) {
        wave.enemies.forEach { data ->
            repeat(data.count) { i ->
                stage.addAction(
                    Actions.sequence(
                        Actions.delay(spawnDelay * i),
                        Actions.run {
                            val enemy = data.factory()
                            val spawnPos = spawnPoints.random()
                            enemy.setPosition(spawnPos.x, spawnPos.y)
                            stage.addActor(enemy)

                            // Tell counter an enemy exists
                            enemyCounter.increaseEnemyCount()

                        }
                    )
                )
            }
        }
    }

    fun setupWaves(newWaves: List<Wave>) {
        waves.clear()
        waves.addAll(newWaves)
        currentWaveIndex = 0
    }
}
