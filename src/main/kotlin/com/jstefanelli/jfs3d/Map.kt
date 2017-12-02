package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import org.joml.Vector3f
import org.joml.Vector4f
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import javax.xml.bind.util.ValidationEventCollector
import kotlin.collections.ArrayList

val floatValuePattern = "([0-9.\\-f]+)"

class Map(val mapFile: InputStream, val interactive: Boolean = false) {


    private val list: ArrayList<Pair<Vector3f, Vector4f>> = ArrayList()
    private val tList: ArrayList<Pair<Vector3f, String>> = ArrayList()
    private val ltList: ArrayList<Pair<Vector3f, Texture>> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

    companion object {
        private val cubeColor = Pattern.compile("^cc\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeRegex = Pattern.compile("^c\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val floorRegex = Pattern.compile("^fl\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val ceilingRegex = Pattern.compile("^ce\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern")
        private val cubeTexture = Pattern.compile("^ct (.+)")
        private val overRegex = Pattern.compile("^over")
    }

    fun parse(){
        val reader = Scanner(mapFile)
        var lastColor = Vector4f(0f, 0f, 1f, 1f)
        var lastTexture = ""
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
                lastTexture = str
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
                    val pair = Pair<Vector3f, String>(pos, lastTexture)
                    tList.add(pair)
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
        for(p in tList){
            val t = Texture(p.second)
            if(!t.load()) {
                System.err.println("Failed to load texture")
                return
            }
            ltList.add(Pair(p.first, t))
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
        for(p in ltList){
            cube.drawTextureAt(p.first, p.second.textureId)
        }
    }
}