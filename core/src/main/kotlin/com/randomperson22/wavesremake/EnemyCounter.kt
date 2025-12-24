package com.randomperson22.wavesremake

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
        checkWaveComplete()
    }

    fun increaseEnemyCount() {
        enemyCount++
    }

    fun resetForNextWave() {
        enemyCount = 0
        cardsShown = false
    }

    private fun checkWaveComplete() {
        if (enemyCount == 0 && !cardsShown) {
            cardsShown = true
            isScanning = false
            onWaveComplete?.invoke()
            showCardPanel?.invoke()
            CardSystem.showRandomCardPanel()
        }
    }
}
