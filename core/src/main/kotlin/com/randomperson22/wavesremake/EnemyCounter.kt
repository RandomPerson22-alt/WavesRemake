package com.randomperson22.wavesremake

import com.badlogic.gdx.Gdx

class EnemyCounter {

    private var enemyCount = 0
    private var isScanning = false
    private var cardsShown = false
    private var waveManager: WaveManager? = null

    var onWaveComplete: (() -> Unit)? = null
    var showCardPanel: (() -> Unit)? = null

    fun setWaveManager(manager: WaveManager) {
        waveManager = manager
    }

    fun decreaseEnemyCount() {
        if (enemyCount <= 0) return
        enemyCount--
        Gdx.app.log("EnemyCounter", "Enemy defeated! $enemyCount remaining")
        checkWaveComplete()
    }

    fun increaseEnemyCount() {
        enemyCount++
        Gdx.app.log("EnemyCounter", "Enemy spawned! $enemyCount total now")
    }

    fun resetForNextWave() {
        enemyCount = 0
        cardsShown = false
    }

    private fun checkWaveComplete() {
        if (enemyCount == 0 && !cardsShown) {
            cardsShown = true
            isScanning = false
            Gdx.app.log("EnemyCounter", "Wave complete!")
            onWaveComplete?.invoke()
            showCardPanel?.invoke()
            CardSystem.showRandomCardPanel()
        }
    }
}
