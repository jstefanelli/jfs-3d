package com.jstefanelli.jfs3d

import org.joml.Vector2f
import org.joml.Vector3f

interface MappableEntity {
    val position: Vector3f
    val size: Vector2f
    val collides: Boolean
    val blocksHit: Boolean
    var active: Boolean
    fun update(map: Map)
    fun draw()
    fun load()
    fun onHit()
    fun onCollide(e: MappableEntity)
}