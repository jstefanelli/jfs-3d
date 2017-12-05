package com.jstefanelli.jfs3d

import org.joml.Vector2f
import org.joml.Vector3f

interface MappableEntity {
    val position: Vector3f
    val size: Vector2f
    var active: Boolean
    fun draw()
    fun load()
    fun onHit()
}