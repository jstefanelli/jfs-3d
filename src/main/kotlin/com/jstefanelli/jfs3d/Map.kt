package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.World
import org.joml.Vector3f
import org.joml.Vector4f
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

val floatValuePattern = "([0-9.\\-f]+)"

class Map(val mapFile: InputStream, val interactive: Boolean = false) {

    private val list: ArrayList<Pair<Vector3f, Vector4f>> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

    companion object {
        private val cubeColor = Pattern.compile("cc +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val cubeRegex = Pattern.compile("c +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val floorRegex = Pattern.compile("fl +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val ceilingRegex = Pattern.compile("ce +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val overRegex = Pattern.compile("over")
    }

    fun parse(){
        val reader = Scanner(mapFile)
        var lastColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
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
                continue
            }
            if(cubeRegex.matcher(line).matches()) {
                val res = cubeRegex.matcher(line)
                res.find()
                val x = res.group(1).toFloat()
                val y = res.group(2).toFloat()
                val z = res.group(3).toFloat()
                val pos = Vector3f(x, y, z)
                val pair = Pair<Vector3f, Vector4f>(pos, lastColor)
                list.add(pair)
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
    }

    fun drawMap(){
        val floor = World.floor ?: return
        val cube = World.cube ?: return
        floor.drawAt(Vector3f(0f, -0.5f, 0f), floorColor)
        floor.drawAt(Vector3f(0f, 0.5f, 0f), ceilColor)
        for(p in list){
            cube.drawColorAt(p.first, p.second)
        }
    }
}