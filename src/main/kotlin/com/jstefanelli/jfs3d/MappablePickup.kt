package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import com.jstefanelli.jfs3d.engine.makeMvp
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils

import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.*

class MappablePickup(val type: MappablePickupType, override val position: Vector3f, override val size: Vector2f, override var active: Boolean) : MappableEntity{

    companion object {
        @JvmStatic
        val TAG = "MappablePickup"
    }

	override val blocksHit: Boolean = false
	override val collides: Boolean = false

    var loaded = false
        get
        private set

    override fun update(map: Map) {

    }

    override fun draw() {
        if(!loaded){
            World.log.warn(TAG, "Loading mid-render. This is not recommended")
            load()
        }

		if(active)
			type.drawAt(position, Vector3f(position.x, position.y, position.z))
    }

    override fun load() {
        if(!type.loaded)
            type.load()

        loaded = true
    }

    override fun onHit() {

    }

	override fun onCollide(e: MappableEntity) {
        if(e is Player) {
	        World.log.log(TAG, "Added ammo")
            e.ammo += 10
	        this.active = false
        }
	}
}

class MappablePickupType(val imageFile: String){

    companion object {
        @JvmStatic
        val TAG = "MappablePickupType"

        @JvmStatic
        var staticLoaded = false
            get
            private set

        @JvmStatic
        private var vbo: Int = 0
        @JvmStatic
        private var tbo: Int = 0

        @JvmStatic
        private val mvp = BufferUtils.createFloatBuffer(16)
        @JvmStatic
        private val uv = BufferUtils.createFloatBuffer(9)

        @JvmStatic
        fun staticLoad(){
            val uvMat: Matrix3f = Matrix3f()
            uvMat.identity()
            uvMat.scale(1f, 1f, 1f)
            uvMat.get(uv)
            uv.position(0)

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

            staticLoaded = true
        }
    }

	var textureObject: Texture? = null
    var loaded = false
        get
        private set

    fun load(){
        if(!staticLoaded)
            staticLoad()

		textureObject = Texture(imageFile)
	    textureObject?.load()

        loaded = true
    }

	fun drawAt(pos: Vector3f, size: Vector3f = Vector3f(1f, 1f, 1f)){

		val t = World.texture ?: return
		val txt = textureObject ?: return



		val rot = Quaternionf()
		rot.rotateAxis(World.playerRotation, 0f, -1f, 0f)
		makeMvp(Vector3f(pos.x, pos.y - .40f, pos.z), rot, mvp, 0, Vector3f(.25f, .25f, .25f))

		glUseProgram(t.programId)
		glUniformMatrix4fv(t.uMvpLoc, false, mvp)
		glUniformMatrix3fv(t.uUvLoc, false, uv)
		glUniform1i(t.uTxtLoc, 0)
		glActiveTexture(GL_TEXTURE0)
		glBindTexture(GL_TEXTURE_2D, txt.textureId)

		glEnableVertexAttribArray(t.aPosLoc)
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glVertexAttribPointer(t.aPosLoc, 3, GL_FLOAT, false, 0, 0)

		glEnableVertexAttribArray(t.aTxtLoc)
		glBindBuffer(GL_ARRAY_BUFFER, tbo)
		glVertexAttribPointer(t.aTxtLoc, 2, GL_FLOAT, false, 0, 0)

		glDrawArrays(GL_TRIANGLES, 0, 6)

		glDisableVertexAttribArray(t.aTxtLoc)
		glDisableVertexAttribArray(t.aPosLoc)
	}

	fun makePickup(pos: Vector3f): MappablePickup{
		return MappablePickup(this, pos, Vector2f(.5f, .5f), true)
	}
}