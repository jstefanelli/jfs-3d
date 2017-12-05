package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Entity
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class TrackedEntity (val type: Entity, override var position: Vector3f, var orientation: Quaternionf = Quaternionf()) : MappableEntity {

    var drawable: Entity.EntityInstance? = null
    override val size: Vector2f = Vector2f(0.5f, 0.5f)
    override var active: Boolean = true
    var lp = 100


    override fun load(){
        if(!type.loaded)
            type.load()

        drawable = type.makeInstance()
    }

    fun update(){
        //Todo: Run behaviour update here
    }

    override fun draw(){
        if(lp > 0) {
            val d = drawable ?: return

            d.drawAt(position, orientation)
        }
    }

    override fun onHit(){
        lp -= 10
        if(lp <= 0){
            active = false
        }
    }

}