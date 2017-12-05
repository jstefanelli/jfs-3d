package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Entity
import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import com.jstefanelli.jfs3d.engine.floatValuePattern
import com.jstefanelli.jfs3d.engine.geometry.rect
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import javax.xml.bind.util.ValidationEventCollector
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Map(val mapFile: InputStream, val interactive: Boolean = false) {

	private val entityList: ArrayList<MappableEntity> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

	private var floorTexture: Texture? = null
	private var ceilTexture: Texture? = null

	private val entityTypes: HashMap<Int, Entity> = HashMap()

	var safetyDistance: Float = 0.03f

    companion object {
        private val cubeColor = Pattern.compile("^cc\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeRegex = Pattern.compile("^c\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val floorRegex = Pattern.compile("^fl\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val ceilingRegex = Pattern.compile("^ce\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeTexture = Pattern.compile("^ct (.+)")
	    private val floorTextureRegex = Pattern.compile("^ft (.+)")
        private val overRegex = Pattern.compile("^over")
		private val entityTypeRegex = Pattern.compile("^et\\s+(\\d+)\\s+(.+)")
		private val entityRegex = Pattern.compile("^e\\s+(\\d+)\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
    }

    fun parse(){
        val reader = Scanner(mapFile)
        var lastColor = Vector4f(0f, 0f, 1f, 1f)
        var lastTexture: Texture? = null
        var mode = false

        while(true){
            if(!interactive && !reader.hasNextLine()) {
                break
            }
            val line: String = reader.nextLine()
            if(overRegex.matcher(line).matches()){
                break
            }
            if(cubeColor.matcher(line).matches()){
                val res = cubeColor.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                lastColor = Vector4f(r, g, b, a)
                mode = false
                continue
            }
            if(cubeTexture.matcher(line).matches()){
                val res = cubeTexture.matcher(line)
                res.find()
                val str = res.group(1)
                mode = true
                lastTexture = Texture(str)
                continue
            }
	        if(floorTextureRegex.matcher(line).matches()){
		        val res = floorTextureRegex.matcher(line)
		        res.find()
		        val str = res.group(1)
		        floorTexture = Texture(str)
		        continue
	        }
            if(cubeRegex.matcher(line).matches()) {
                val res = cubeRegex.matcher(line)
                res.find()
                val x = res.group(1).toFloat()
                val y = res.group(2).toFloat()
                val z = res.group(3).toFloat()
                val pos = Vector3f(x, y, z)
                if(!mode) {
                    entityList.add(MappedCube(pos, lastColor))
                }else{
                    entityList.add(MappedCube(pos, lastTexture ?: return))
                }
                continue
            }
            if(floorRegex.matcher(line).matches()){
                val res = floorRegex.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                floorColor = Vector4f(r, g, b, a)
                continue
            }
            if(ceilingRegex.matcher(line).matches()){

                val res = ceilingRegex.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                ceilColor = Vector4f(r, g, b, a)
                continue
            }
			if(entityTypeRegex.matcher(line).matches()){
				val res = entityTypeRegex.matcher(line)
				res.find()
				val index = res.group(1).toInt()
				if(entityTypes.containsKey(index)){
					System.err.println("Found duplicate entity type keys. Skipping...")
					continue
                }
				val path = res.group(2)
				val et = Entity(path)
				entityTypes.put(index, et)
            }
			if(entityRegex.matcher(line).matches()){
				val res = entityRegex.matcher(line)
				res.find()
				val index = res.group(1).toInt()
				if(!entityTypes.containsKey(index)){
					System.err.println("Entity type key not found. Skipping.,,")
					continue
                }
				val x = res.group(2).toFloat()
				val y = res.group(3).toFloat()
				val z = res.group(4).toFloat()
				entityList.add(TrackedEntity(entityTypes.get(index) ?: return, Vector3f(x, y, z)))
            }
        }
        for(p in entityList){
            p.load()
        }
	    val l = floorTexture?.load()
		if(l == false) System.err.println("Failed to load floor texture")

    }

    fun drawMap(){
        val floor = World.floor ?: return
	    if(floorTexture == null)
            floor.drawAt(Vector3f(0f, -0.5f, 0f), floorColor)
	    else {
		    val tx = floorTexture ?: return
		    floor.drawAt(Vector3f(0f, -0.5f, 0f), tx.textureId)
	    }
	    if(ceilTexture == null)
            floor.drawAt(Vector3f(0f, 0.5f, 0f), ceilColor)
	    else{
		    val tx = ceilTexture ?: return
		    floor.drawAt(Vector3f(0f, 0.5f, 0f), tx.textureId)
	    }
	    synchronized(entityList) {
		    for (p in entityList) {
				p.draw()
		    }
	    }

    }

    fun validateMovement(position: Vector3f): Boolean{
	    synchronized(entityList) {
		    for (c in entityList) {
			    if (c.active && position.x >= c.position.x - c.size.x / 2 - safetyDistance && position.x <= c.position.x + c.size.x / 2 + safetyDistance
					    && position.z >= c.position.z - c.size.y / 2 - safetyDistance && position.z <= c.position.z + c.size.y / 2 + safetyDistance)
				    return false
		    }
	    }
        return true
    }



	fun myDistance(pos: Vector3f, rot: Quaternionf, pos2: Vector3f) : Float{
		val dist = pos2.distance(pos)
		val forward = Vector3f(0f, 0f, -dist)
		val pos2x = Vector3f(pos2)
		pos2x.sub(pos)
		forward.rotate(rot)

		if(pos2x.x * forward.x >= 0f && pos2x.z * forward.z >= 0f)
			return dist
		else
			return -dist
	}

	fun rayCast(position: Vector3f, orientation: Quaternionf) : Pair<Vector3f, Float>{

		var lastDist = Float.MAX_VALUE
		var lastPoint = Vector3f()

		val forward = Vector3f(0f, 0f, -1f)

		forward.rotate(orientation)
		forward.add(position)
		val line = rect(position, forward)
		var lastHitEntity: MappableEntity? = null
		synchronized(entityList) {
			for (c in entityList) {
				if(!c.active)
					continue
				val x0 = line.getX(c.position.z - c.size.y / 2)
				val x1 = line.getX(c.position.z + c.size.y / 2)

				if (x0 >= c.position.x - c.size.x / 2 && x0 <= c.position.x + c.size.x / 2) {
					val pt = Vector3f(x0, 0f, c.position.z - c.size.y / 2)
					val dist = myDistance(position, orientation, pt)

					if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
						lastDist = dist
						lastPoint = pt
					}
				}
				if (x1 >= c.position.x - c.size.x / 2 && x1 <= c.position.x + c.size.x / 2) {
					val pt = Vector3f(x1, 0f, c.position.z + c.size.y / 2)
					val dist = myDistance(position, orientation, pt)

					if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
						lastDist = dist
						lastPoint = pt
					}
				}
				val y0 = line.getY(c.position.x - c.size.x / 2)
				val y1 = line.getY(c.position.x + c.size.x / 2)

				if (y0 >= c.position.z - c.size.y / 2 && y0 <= c.position.z + c.size.y / 2) {
					val pt = Vector3f(c.position.x - c.size.x / 2, 0f, y0)
					val dist = myDistance(position, orientation, pt)

					if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
						lastDist = dist
						lastPoint = pt
					}
				}
				if (y1 >= c.position.z - c.size.y / 2 && y1 <= c.position.z + c.size.y / 2) {
					val pt = Vector3f(c.position.x + c.size.x / 2, 0f, y1)
					val dist = myDistance(position, orientation, pt)

					if(dist >= 0 && dist < lastDist) {
						lastHitEntity = c
						lastDist = dist
						lastPoint = pt
					}
				}
			}
		}
		lastHitEntity?.onHit()
		return Pair(lastPoint, lastDist)
	}
}