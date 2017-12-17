package com.jstefanelli.jfs3d

import org.joml.Vector2f
import org.joml.Vector3f

class MappablePickup(val type: MappablePickupType, override val position: Vector3f, override val size: Vector2f, override var active: Boolean) : MappableEntity{

    companion object {

    }

    override fun update(map: Map) {

    }

    override fun draw() {

    }

    override fun load() {

    }

    override fun onHit() {

    }
}

class MappablePickupType{

    companion object {

    }
}