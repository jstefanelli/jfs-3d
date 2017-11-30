package com.jstefanelli.jfs3d

import org.joml.Vector3f
import org.joml.Vector4f
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

val floatValuePattern = "([0-9.\\-f]+)"

class Map(val mapFile: InputStream, val interactive: Boolean = false) {

    private val list: ArrayList<Pair<Vector3f, Vector4f>> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

    companion object {
        private val cubeColor = Regex("cc +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val cubeRegex = Regex("c +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val floorRegex = Regex("fl +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val ceilingRegex = Regex("ce +$floatValuePattern +$floatValuePattern +$floatValuePattern +$floatValuePattern")
        private val overRegex = Regex("over")
    }

    fun parse(){
        val reader = Scanner(mapFile)
        var lastColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
        while(true){
            if(!interactive && !reader.hasNextLine()) {
                break
            }
            val line: String = reader.nextLine()
            if(overRegex.matches(line)){
                break
            }
            if(cubeColor.matches(line)){
                val res = cubeColor.toPattern().matcher(line).toMatchResult()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                lastColor = Vector4f(r, g, b, a)
                continue
            }
            if(cubeRegex.matches(line)) {
                val res = cubeRegex.toPattern().matcher(line).toMatchResult()
                val x = res.group(0).toFloat()
                val y = res.group(1).toFloat()
                val z = res.group(2).toFloat()
                val pos = Vector3f(x, y, z)
                val pair = Pair<Vector3f, Vector4f>(pos, lastColor)
                list.add(pair)
                continue
            }
            if(floorRegex.matches(line)){
                val res = floorRegex.toPattern().matcher(line).toMatchResult()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                floorColor = Vector4f(r, g, b, a)
                continue
            }
            if(ceilingRegex.matches(line)){

                val res = ceilingRegex.toPattern().matcher(line).toMatchResult()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                ceilColor = Vector4f(r, g, b, a)
                continue
            }
        }

    }
}