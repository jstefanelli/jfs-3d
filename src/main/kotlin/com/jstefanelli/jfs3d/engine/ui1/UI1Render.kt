package com.jstefanelli.jfs3d.engine.ui1

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import com.jstefanelli.jfs3d.engine.makeMvp
import com.jstefanelli.jfs3d.engine.utils.TextureFactory
import org.joml.Vector2f
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL11.*

class UI1Render(val utils: UI1Utils) {

	companion object {
		@JvmStatic
		val TAG = "UI1Render"

		@JvmStatic
		private var staticLoaded = false

		@JvmStatic
		private val mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)

		@JvmStatic
		private var cubeVbo = 0

		@JvmStatic
		private var cubeTbo = 0


		@JvmStatic
		fun staticLoad(){
			if(staticLoaded)
				return

			cubeVbo = glGenBuffers()
			glBindBuffer(GL_ARRAY_BUFFER, cubeVbo)
			glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
					-0.5f, -0.5f, 0.0f,
					-0.5f, 0.5f, 0.0f,
					0.5f, -0.5f, 0.0f,

					-0.5f, 0.5f, 0.0f,
					0.5f, 0.5f, 0.0f,
					0.5f, -0.5f, 0.0f
			), GL_STATIC_DRAW)

			cubeTbo = glGenBuffers()
			glBindBuffer(GL_ARRAY_BUFFER, cubeTbo)
			glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
					0.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 0.0f,

					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f
			), GL_STATIC_DRAW)

			staticLoaded = true
		}
	}

	fun preloadTextures(textures: Array<String>){
		for(s in textures){
			val t = TextureFactory.getTexture(s)
			t?.load()
		}
	}

	fun drawTextureQuadAt(posPx: Vector2f, sizePx: Vector2f, texture: Texture?){
		if(!staticLoaded)
			staticLoad()

		val t = World.texture ?: return

		utils.makeMvp(posPx, sizePx, mvp, 0)


	}
}