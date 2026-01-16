package com.randomperson22.wavesremake

import com.badlogic.gdx.graphics.Texture

class Enemy1(
    player: Player,
    enemyCounter: EnemyCounter
) : EnemyBase(
    player,
    enemyCounter,
    sprite1Texture = AssetLoader.manager.get("enemy1.png", Texture::class.java),
    sprite2Texture = AssetLoader.manager.get("enemy1Walk.png", Texture::class.java)
) {
    override var moveSpeed = 125f
    override var health = 5f
    override var damageAmount = 1.3f
}
