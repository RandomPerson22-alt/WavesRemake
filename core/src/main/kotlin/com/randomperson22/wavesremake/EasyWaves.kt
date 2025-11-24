package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

object EasyWaves {
    fun create(player: Player, counter: EnemyCounter, assets: Map<String, Texture>): List<WaveManager.Wave> {
        return listOf(
            WaveManager.Wave(
                "Wave 1",
                listOf(
                    WaveManager.EnemySpawnData(factory = { Enemy1(player, counter, assets) }, count = 3)
                )
            ),
            WaveManager.Wave(
                "Wave 2",
                listOf(
                    WaveManager.EnemySpawnData(factory = { Enemy1(player, counter, assets) }, count = 5)
                )
            ),
            WaveManager.Wave(
                "Wave 3",
                listOf(
                    WaveManager.EnemySpawnData(factory = { Enemy1(player, counter, assets) }, count = 7)
                )
            ),
            WaveManager.Wave(
                "Wave 3",
                listOf(
                    WaveManager.EnemySpawnData(factory = { Enemy1(player, counter, assets) }, count = 10)
                )
            ),
        )
    }
}
