package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.World
import org.joml.Vector2f
import org.joml.Vector3f

class MappablePickup(val type: MappablePickupType, override val position: Vector3f, override val size: Vector2f, override var active: Boolean) : MappableEntity{

    companion object {
        @JvmStatic
        val TAG = "MappablePickup"
    }

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
			type.drawAt(position, Vector3f(size.x, size.y, 1f))
    }

    override fun load() {
        if(!type.loaded)
            type.load()

        loaded = true
    }

    override fun onHit() {

    }
}

class MappablePickupType{

    companion object {
        @JvmStatic
        val TAG = "MappablePickupType"

        @JvmStatic
        var staticLoaded = false
            get
            private set

        @JvmStatic
        fun staticLoad(){

            staticLoaded = true
        }
    }

    var loaded = false
        get
        private set

    fun load(){
        if(!staticLoaded)
            staticLoad()



        loaded = true
    }

	fun drawAt(pos: Vector3f, size: Vector3f = Vector3f(1f, 1f, 1f)){

	}
}