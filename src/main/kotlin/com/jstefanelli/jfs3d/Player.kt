package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.World
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class Player : MappableEntity{
    override var position: Vector3f = Vector3f(0f, 0f, 0f)
    override val size: Vector2f = Vector2f(.5f, .5f)
    var orientation: Quaternionf = Quaternionf()
    override var active: Boolean = true
	override val blocksHit: Boolean = false
	override val collides: Boolean = false
    var lp = 100000
    var ammo = 100

    override fun update(map: Map){

    }

    override fun draw(){

    }

    override fun onHit(){
        lp -= 10
        System.out.println("Player hit: " + lp)
        if(lp <= 0){
            System.err.println("Player dead. Game over.")
            World.currentWindow?.close()
            return
        }
    }

    override fun load(){

    }

    override fun toString(): String {
        return "Player at: " + position
    }

	override fun onCollide(e: MappableEntity) {
		//Unused
	}
}