package com.jstefanelli.jfs3d.engine

import com.jstefanelli.jfs3d.engine.geometry.rect
import org.joml.*
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
        private val do_anglesPattern = Pattern.compile("^do_angles\\s+(true|false)")
        private val angle0Pattern = Pattern.compile("^angle_0\\s+(.+)")
        private val angle1Pattern = Pattern.compile("^angle_1\\s+(.+)")
        private val angle2Pattern = Pattern.compile("^angle_2\\s+(.+)")
        private val angle3Pattern = Pattern.compile("^angle_3\\s+(.+)")
        private val angle4Pattern = Pattern.compile("^angle_4\\s+(.+)")
        private val angle5Pattern = Pattern.compile("^angle_5\\s+(.+)")
        private val angle6Pattern = Pattern.compile("^angle_6\\s+(.+)")
        private val angle7Pattern = Pattern.compile("^angle_7\\s+(.+)")
        private val uvSizePattern = Pattern.compile("^uvSize\\s+$floatValuePattern\\s+$floatValuePattern")

	    @JvmStatic
	    val TAG = "Entity"
    }

    var do_angles: Boolean = true
        get
        private set
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

    var loaded = false
        get
        private set

    fun load(){
        if(loaded) return



        val file = File(definitionFilePath)

        if(!file.exists()){
            World.log.err(TAG, "Failed to load entity: missing definition file ($definitionFilePath)")
            return
        }

        Scanner(FileInputStream(file)).use {
            while(it.hasNextLine()){
                val line = it.nextLine()
                if(do_anglesPattern.matcher(line).matches()){
                    val res = do_anglesPattern.matcher(line)
                    res.find()
                    do_angles = res.group(1).toBoolean()
                }
                if(angle0Pattern.matcher(line).matches()){
                    val res = angle0Pattern.matcher(line)
                    res.find()
                    angle0Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle1Pattern.matcher(line).matches() && do_angles){
                    val res = angle1Pattern.matcher(line)
                    res.find()
                    angle1Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle2Pattern.matcher(line).matches() && do_angles){
                    val res = angle2Pattern.matcher(line)
                    res.find()
                    angle2Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle3Pattern.matcher(line).matches() && do_angles){
                    val res = angle3Pattern.matcher(line)
                    res.find()
                    angle3Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle4Pattern.matcher(line).matches() && do_angles){
                    val res = angle4Pattern.matcher(line)
                    res.find()
                    angle4Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle5Pattern.matcher(line).matches() && do_angles){
                    val res = angle5Pattern.matcher(line)
                    res.find()
                    angle5Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle6Pattern.matcher(line).matches() && do_angles){
                    val res = angle6Pattern.matcher(line)
                    res.find()
                    angle6Texture = Texture(file.parent + File.separator + res.group(1))
                    continue
                }
                if(angle7Pattern.matcher(line).matches() && do_angles){
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
            World.log.err(TAG, "Angle 0 not found. Entity loading failed")
            return
        }else{
            angle0Texture?.load()
        }
        if(do_angles) {
	        if (angle1Texture == null) {
		        World.log.err(TAG, "Angle 1 not found. Entity loading failed")
		        return
	        } else {
		        angle1Texture?.load()
	        }

	        if (angle2Texture == null) {
		        World.log.err(TAG, "Angle 2 not found. Entity loading failed")
		        return
	        } else {
		        angle2Texture?.load()
	        }

	        if (angle3Texture == null) {
		        World.log.err(TAG, "Angle 3 not found. Entity loading failed")
		        return
	        } else {
		        angle3Texture?.load()
	        }

	        if (angle4Texture == null) {
		        World.log.err(TAG, "Angle 4 not found. Entity loading failed")
		        return
	        } else {
		        angle4Texture?.load()
	        }

	        if (angle5Texture == null) {
		        World.log.err(TAG, "Angle 5 not found. Entity loading failed")
		        return
	        } else {
		        angle5Texture?.load()
	        }

	        if (angle6Texture == null) {
		        World.log.err(TAG, "Angle 6 not found. Entity loading failed")
		        return
	        } else {
		        angle6Texture?.load()
	        }

	        if (angle7Texture == null) {
		        World.log.err(TAG, "Angle 7 not found. Entity loading failed")
		        return
	        } else {
		        angle7Texture?.load()
	        }
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

            val afo = Mathf.angleFromOrigin(playerRelative)
            var angle = Mathf.toGrad(afo)
            val ax = AxisAngle4f(orientation)
            if(ax.angle != 0f) {
	            angle -= Mathf.toGrad(ax.y * ax.angle)
            }
            while(angle < 0)
                angle += 360f
            while(angle > 360.0f)
                angle -= 360.0f
            if(baseType.do_angles) {
	            if ((angle in 0f..22.5f) || (angle in 337.5f..360.0f)) {
		            return baseType.angle0Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if ((angle in 22.5f..67.5f)) {
		            return baseType.angle7Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if ((angle in 67.5f..112.5f)) {
		            return baseType.angle6Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if (angle in 112.5f..157.5f) {
		            return baseType.angle5Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if (angle in 157.5f..202.5f) {
		            return baseType.angle4Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if (angle in 202.5f..247.5f) {
		            return baseType.angle3Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if (angle in 247.5f..292.5f) {
		            return baseType.angle2Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
	            if (angle in 292.5f..337.5f) {
		            return baseType.angle1Texture ?: throw RuntimeException("Entity: Required texture not found")
	            }
            }else{
                return baseType.angle0Texture ?: throw RuntimeException("Entity: Required texture not found")
            }
            World.log.err("ENTITY_BASE", "Failed to detect angle: " + angle)
            return baseType.angle0Texture ?: throw RuntimeException("Entity: Required Texture not loaded")
        }

        fun drawAt(position: Vector3f, orientation: Quaternionf, scale: Vector2f = Vector2f(1.0f, 1.0f)){
            if(!baseType.loaded) return

            val t = World.texture ?: return

            val rot = Quaternionf()
            rot.rotateAxis(-World.playerRotation, 0f, 1f, 0f)
            makeMvp(position, rot, mvp, 0, Vector3f(scale.x, scale.y, 1f))

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