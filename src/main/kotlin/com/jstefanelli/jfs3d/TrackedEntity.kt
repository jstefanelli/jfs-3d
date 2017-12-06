package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Entity
import com.jstefanelli.jfs3d.engine.Mathf
import com.jstefanelli.jfs3d.engine.World
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class TrackedEntity (val type: Entity, override var position: Vector3f, var orientation: Quaternionf = Quaternionf()) : MappableEntity {

    var drawable: Entity.EntityInstance? = null
    override val size: Vector2f = Vector2f(0.5f, 0.5f)
    override var active: Boolean = true
    var lp = 100
    var mvmtSpeed = 0.01f

    private var lastShot = 0L
    var fireRate = 2500L

    override fun load(){
        if(!type.loaded)
            type.load()

        drawable = type.makeInstance()
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

    override fun update(map: Map){
        if(!active)
            return
        val mvmt = Vector3f(0f, 0f, -mvmtSpeed)
        val targetPos = Vector3f(World.playerPosition)
        targetPos.sub(position)
        var angle = Mathf.angleFromOrigin(targetPos)

        //if(angle < 0)
            //angle += Mathf.Pif * 2f

        val myOrientation = Quaternionf()
        myOrientation.rotateAxis(angle, 0f, 1f, 0f)
        mvmt.rotate(myOrientation)

        val p = map.rayCastToEntity(position, myOrientation, false, this)

        World.log.log("ENTITY", "RayCast result: " + (p.first?.toString() ?: "not found"))
        if(p.first ?: return == map.player){
            World.log.log("ENTITY", "Found player")

            orientation = myOrientation

            //mvmt.rotate(myOrientation)
            mvmt.add(position)

            if(map.validateMovement(mvmt, true, this)){
                position = mvmt
                World.log.log("ENTITY", "Moved entity to: " + mvmt)
            }

            val time = System.currentTimeMillis()
            if(time - lastShot > fireRate){
                map.player.onHit()
                lastShot = time
                val ex = map.explosions
                val e = map.explosion
                val inst = e?.makeInstance() ?: return
                ex.add(Pair(position, inst))
            }



        }


    }

    override fun toString() : String{
        return "Entity at: " + position
    }
}