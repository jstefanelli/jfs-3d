package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import com.jstefanelli.jfs3d.engine.floatValuePattern
import org.joml.Vector3f
import org.joml.Vector4f
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import javax.xml.bind.util.ValidationEventCollector
import kotlin.collections.ArrayList


class Map(val mapFile: InputStream, val interactive: Boolean = false) {


    private val list: ArrayList<Pair<Vector3f, Vector4f>> = ArrayList()
    private val ltList: ArrayList<Pair<Vector3f, Texture>> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

	private var floorTexture: Texture? = null
	private var ceilTexture: Texture? = null


	var safetyDistance: Float = 0.03f

    companion object {
        private val cubeColor = Pattern.compile("^cc\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeRegex = Pattern.compile("^c\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val floorRegex = Pattern.compile("^fl\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val ceilingRegex = Pattern.compile("^ce\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeTexture = Pattern.compile("^ct (.+)")
	    private val floorTextureRegex = Pattern.compile("^ft (.+)")
        private val overRegex = Pattern.compile("^over")
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
                    val pair = Pair<Vector3f, Vector4f>(pos, lastColor)
                    list.add(pair)
                }else{
                    val pair = Pair<Vector3f, Texture>(pos, lastTexture!!)
                    ltList.add(pair)
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
        }
        for(p in ltList){
            p.second.load()
        }
	    val l = floorTexture?.load()
	    if(l == false) System.err.println("Failed to load floor texture")

    }

    fun drawMap(){
        val floor = World.floor ?: return
        val cube = World.cube ?: return
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
        for(p in list){
            cube.drawColorAt(p.first, p.second)
        }
        for(p in ltList){
            cube.drawTextureAt(p.first, p.second.textureId)
        }
    }

    fun validateMovement(position: Vector3f): Boolean{
        for(c in list){
            if(position.x >= c.first.x - 0.5f - safetyDistance && position.x <= c.first.x + 0.5f + safetyDistance
                    && position.z >= c.first.z - 0.5f - safetyDistance && position.z <= c.first.z + 0.5f + safetyDistance)
                return false
        }
	    for(c in ltList){
		    if(position.x >= c.first.x - 0.5f - safetyDistance && position.x <= c.first.x + 0.5f + safetyDistance
				    && position.z >= c.first.z - safetyDistance - 0.5f && position.z <= c.first.z + 0.5f + safetyDistance)
			    return false
	    }

        return true
    }
}