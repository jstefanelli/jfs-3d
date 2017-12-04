package com.jstefanelli.jfs3d.engine

import com.jstefanelli.jfs3d.engine.geometry.rect
import org.joml.AxisAngle4f
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import java.io.File
import java.io.FileInputStream
import java.nio.FloatBuffer
import java.util.*
import java.util.regex.Pattern

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*

class Entity(val definitionFilePath: String) {
    companion object {
        private val angle0Pattern = Pattern.compile("^angle_0\\s+(.+)")
        private val angle1Pattern = Pattern.compile("^angle_1\\s+(.+)")
        private val angle2Pattern = Pattern.compile("^angle_2\\s+(.+)")
        private val angle3Pattern = Pattern.compile("^angle_3\\s+(.+)")
        private val angle4Pattern = Pattern.compile("^angle_4\\s+(.+)")
        private val angle5Pattern = Pattern.compile("^angle_5\\s+(.+)")
        private val angle6Pattern = Pattern.compile("^angle_6\\s+(.+)")
        private val angle7Pattern = Pattern.compile("^angle_7\\s+(.+)")
        private val uvSizePattern = Pattern.compile("^uvSize\\s+$floatValuePattern\\s+$floatValuePattern")
    }

    var angle0Texture: Texture? = null
    var angle1Texture: Texture? = null
    var angle2Texture: Texture? = null
    var angle3Texture: Texture? = null
    var angle4Texture: Texture? = null
    var angle5Texture: Texture? = null
    var angle6Texture: Texture? = null
    var angle7Texture: Texture? = null

    var uvSizeX: Float = 1f
    var uvSizeY: Float = 1f

    var vbo: Int = 0
    var tbo: Int = 0
    var uvFb: FloatBuffer = BufferUtils.createFloatBuffer(9)

    private var loaded = false

    fun load(){
        if(loaded) return



        val file = File(definitionFilePath)

        if(!file.exists()){
            System.err.println("Failed to load entity: missing definition file")
            return
        }

        Scanner(FileInputStream(file)).use {
            while(it.hasNextLine()){
                val line = it.nextLine()
                if(angle0Pattern.matcher(line).matches()){
                    val res = angle0Pattern.matcher(line)
                    res.find()
                    angle0Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle1Pattern.matcher(line).matches()){
                    val res = angle1Pattern.matcher(line)
                    res.find()
                    angle1Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle2Pattern.matcher(line).matches()){
                    val res = angle2Pattern.matcher(line)
                    res.find()
                    angle2Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle3Pattern.matcher(line).matches()){
                    val res = angle3Pattern.matcher(line)
                    res.find()
                    angle3Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle4Pattern.matcher(line).matches()){
                    val res = angle4Pattern.matcher(line)
                    res.find()
                    angle4Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle5Pattern.matcher(line).matches()){
                    val res = angle5Pattern.matcher(line)
                    res.find()
                    angle5Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle6Pattern.matcher(line).matches()){
                    val res = angle6Pattern.matcher(line)
                    res.find()
                    angle6Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle7Pattern.matcher(line).matches()){
                    val res = angle7Pattern.matcher(line)
                    res.find()
                    angle7Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(uvSizePattern.matcher(line).matches()){
                    val res = uvSizePattern.matcher(line)
                    res.find()
                    uvSizeX = res.group(1).toFloat()
                    uvSizeY = res.group(2).toFloat()
                    continue
                }
            }
        }

        if(angle0Texture == null){
            System.err.println("Angle 0 not found. Entity loading failed")
            return
        }else{
            angle0Texture?.load()
        }

        if(angle1Texture == null){
            System.err.println("Angle 1 not found. Entity loading failed")
            return
        }else{
            angle1Texture?.load()
        }

        if(angle2Texture == null){
            System.err.println("Angle 2 not found. Entity loading failed")
            return
        }else{
            angle2Texture?.load()
        }

        if(angle3Texture == null){
            System.err.println("Angle 3 not found. Entity loading failed")
            return
        }else{
            angle3Texture?.load()
        }

        if(angle4Texture == null){
            System.err.println("Angle 4 not found. Entity loading failed")
            return
        }else{
            angle4Texture?.load()
        }

        if(angle5Texture == null){
            System.err.println("Angle 5 not found. Entity loading failed")
            return
        }else{
            angle5Texture?.load()
        }

        if(angle6Texture == null){
            System.err.println("Angle 6 not found. Entity loading failed")
            return
        }else{
            angle6Texture?.load()
        }

        if(angle7Texture == null){
            System.err.println("Angle 7 not found. Entity loading failed")
            return
        }else{
            angle7Texture?.load()
        }

        val uvMat = Matrix3f()
        uvMat.scale(uvSizeX, uvSizeY, 1f)
        uvMat.get(uvFb)
        uvFb.position(0)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                -.5f, -.5f, 0f,
                -.5f, .5f, 0f,
                .5f, -.5f, 0f,

                -.5f, .5f, 0f,
                .5f, .5f, 0f,
                .5f, -.5f, 0f
        ), GL_STATIC_DRAW)


        tbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, tbo)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                0f, 0f,
                0f, 1f,
                1f, 0f,

                0f, 1f,
                1f, 1f,
                1f, 0f
        ), GL_STATIC_DRAW)

        loaded = true
    }

    fun makeInstance() : EntityInstance{
        return EntityInstance(this)
    }

    class EntityInstance constructor(val baseType: Entity){

        companion object {
            private val mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)
        }

        private fun getAngle(position: Vector3f, orientation: Quaternionf): Texture{
            val playerRelative = Vector3f(World.playerPosition)
            playerRelative.sub(position)

            var angle = Mathf.toGrad(Mathf.angleFromOrigin(playerRelative))
            val ax = AxisAngle4f(orientation)
            angle += Mathf.toGrad(ax.y * ax.angle)
            if(angle < 0)
                angle += 360f
            if(angle > 360.0f)
                angle -= 360.0f
            System.out.println("Angle: " + angle)


            if((angle >= 0 && angle < 22.5f) || (angle <= 360.0f && angle >= 337.5f)){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle4Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle0Texture ?: throw RuntimeException("Entity: Required texture not found")
            }

            if((angle >= 22.5f && angle < 67.5)){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle3Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle1Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if((angle >= 67.5f && angle < 112.5f)){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle2Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle2Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if(angle >= 112.5f && angle < 157.5f){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle1Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle3Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if(angle >= 157.5f && angle < 202.5f){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle0Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle4Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if(angle >= 202.5f && angle < 247.5f){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle7Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle5Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if(angle >= 247.5f && angle < 292.5f){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle6Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle6Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            if(angle >= 292.5f && angle < 337.5f){
                //return if(Mathf.concord(playerRelative, objectForward))
                    return baseType.angle5Texture ?: throw RuntimeException("Entity: Required texture not found")
                //else
                    //baseType.angle7Texture ?: throw RuntimeException("Entity: Required texture not found")
            }

            return baseType.angle0Texture ?: throw RuntimeException("Entity: Required Texture not loaded")
        }

        fun drawAt(position: Vector3f, orientation: Quaternionf){
            if(!baseType.loaded) return

            val t = World.texture ?: return

            val rot = Quaternionf()
            rot.rotateAxis(-World.playerRotation, 0f, 1f, 0f)
            makeMvp(position, rot, mvp, 0)

            glUseProgram(t.programId)
            glUniformMatrix4fv(t.uMvpLoc, false, mvp)
            glUniformMatrix3fv(t.uUvLoc, false, baseType.uvFb)
            glUniform1i(t.uTxtLoc, 0)
            glActiveTexture(GL_TEXTURE0)
            val tx = getAngle(position, orientation)
            glBindTexture(GL_TEXTURE_2D, tx.textureId)

            glEnableVertexAttribArray(t.aPosLoc)
            glBindBuffer(GL_ARRAY_BUFFER, baseType.vbo)
            glVertexAttribPointer(t.aPosLoc, 3, GL_FLOAT, false, 0, 0)

            glEnableVertexAttribArray(t.aTxtLoc)
            glBindBuffer(GL_ARRAY_BUFFER, baseType.tbo)
            glVertexAttribPointer(t.aTxtLoc, 2, GL_FLOAT, false, 0, 0)

            glDrawArrays(GL_TRIANGLES, 0, 6)

            glDisableVertexAttribArray(t.aTxtLoc)
            glDisableVertexAttribArray(t.aPosLoc)

        }
    }
}