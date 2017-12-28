package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

class MappedCube private constructor(override var position: Vector3f) : MappableEntity {

    override val size: Vector2f = Vector2f(1f, 1f)
    var useTexture = false
        get
        private set
    var txt: Texture? = null
    var col: Vector4f? = null
    override var active: Boolean = true
	override val blocksHit: Boolean = true
	override val collides: Boolean = true

    var loaded = false
        get
        private set

    constructor(position: Vector3f, txt: Texture) : this(position){
        this.txt = txt
        useTexture = true
    }

    constructor(position: Vector3f, color: Vector4f) : this(position){
        col = color
        useTexture = false
    }

    override fun load(){
        if(loaded)
            return

        txt?.load()

        val c = World.cube ?: return
        if(!c.loaded) c.load()
    }

    override fun draw(){
        if(active) {
            val c = World.cube ?: return
            if (useTexture) {
                val t = txt ?: return
                c.drawTextureAt(position, t.textureId)
            } else {
                val cl = col ?: return
                c.drawColorAt(position, cl)
            }
        }
    }

    override fun toString(): String{
        return "Cube at " + position
    }

    override fun onHit(){

    }

    override fun update(map: Map){

    }

	override fun onCollide(e: MappableEntity) {

	}
}