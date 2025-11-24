package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

class Enemy1(
    player: Player,
    enemyCounter: EnemyCounter,
    loadedAssets: Map<String, Texture>
) : EnemyBase(
    player,
    enemyCounter,
    sprite1Texture = loadedAssets["enemy1.png"] ?: error("enemy1.png not loaded!"),
    sprite2Texture = loadedAssets["enemy1Walk.png"] ?: error("enemy1Walk.png not loaded!")
) {
    override var moveSpeed = 100f
    override var health = 5f
    override var damageAmount = 1.3f
}
