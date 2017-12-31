package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Entity
import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import com.jstefanelli.jfs3d.engine.makeMvp
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils

import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.*

class MappablePickup(val type: Entity, override val position: Vector3f, override val size: Vector2f, override var active: Boolean) : MappableEntity{

    companion object {
        @JvmStatic
        val TAG = "MappablePickup"
    }

	var drawable: Entity.EntityInstance? = null
	override val blocksHit: Boolean = false
	override val collides: Boolean = false

    var loaded = false
        get
        private set

    override fun update(map: Map) {

    }

    override fun draw() {
        if(!loaded){
            World.log.warn(TAG, "Loading mid-render. This is not recommended")
            load()
        }

		if(active)
			drawable?.drawAt(position, Quaternionf(), size)
    }

    override fun load() {
        if(!type.loaded)
            type.load()

	    drawable = type.makeInstance()

        loaded = true
    }

    override fun onHit() {

    }

	override fun onCollide(e: MappableEntity) {
        if(e is Player) {
	        World.log.log(TAG, "Added ammo")
            e.ammo += 10
	        this.active = false
        }
	}
}

