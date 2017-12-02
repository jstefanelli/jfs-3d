package com.jstefanelli.jfs3d.engine

import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.regex.Pattern

class JSpriteType(val filePath: String) {

	companion object {
		val frameRegex: Pattern = Pattern.compile("^frame\\s+(.+)")
		val frameTimeRegex: Pattern = Pattern.compile("^time\\s+$floatValuePattern")
		val repeatRegex: Pattern = Pattern.compile("^repeat\\s+(true|false)")
	}

	var numberOfFrames = 0
		get
		private set

	var frameTime = 0f
		get
		private set


	var repeat = false
		get
		private set


	var framesList: ArrayList<Texture> = ArrayList()
		get
		private set

	private var loaded = false

	class JSpriteInstance(private val type: JSpriteType){


		companion object {
			private var vbo: Int = 0
			private var tbo: Int = 0
			private var mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)
			private var uv: FloatBuffer = BufferUtils.createFloatBuffer(9)

			private var loaded = false

			fun initialize(){
				if(loaded)
					return

				vbo = glGenBuffers()
				glBindBuffer(GL_ARRAY_BUFFER, vbo)
				glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
						-0.5f, -0.5f, 0f,
						-0.5f, 0.5f, 0f,
						0.5f, -0.5f, 0f,

						-0.5f, 0.5f, 0f,
						0.5f, 0.5f, 0f,
						0.5f, -0.5f, 0f
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

				val uvMat = Matrix3f()
				uvMat.identity()
				uvMat.get(uv)
				uv.position(0)

				loaded = true
			}
		}

		var running: Boolean = true
		private var lastTimeRun = 0L
		private var currentFrame = 0

		fun drawAt(position: Vector3f){
			if(!running)
				return

			if(!loaded)
				initialize()

			if(lastTimeRun == 0L)
				lastTimeRun = System.currentTimeMillis()

			val currentTime = System.currentTimeMillis()
			if((currentTime - lastTimeRun) >= type.frameTime * 1000) {
				currentFrame++
				lastTimeRun = currentTime
			}

			if(currentFrame >= type.numberOfFrames){
				if(!type.repeat) {
					running = false
					return
				}else{
					currentFrame = 0
				}
			}

			val t = World.texture ?: return

			val rotation = Quaternionf()
			rotation.rotateAxis(-World.playerRotation, 0f, 1f, 0f)
			mvp.position(0)
			makeMvp(position, rotation, mvp, 0, Vector3f(0.25f, 0.25f, 0.25f))

			glUseProgram(t.programId)
			glUniformMatrix4fv(t.uMvpLoc, false, mvp)
			glUniformMatrix3fv(t.uUvLoc, false, uv)

			glUniform1i(t.uTxtLoc, 0)
			glActiveTexture(GL_TEXTURE0)
			glBindTexture(GL_TEXTURE_2D, type.framesList[currentFrame].textureId)

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
	}

	fun makeInstance(): JSpriteInstance{
		if(!loaded) load()
		return JSpriteInstance(this)
	}

	fun load(){
		if(loaded) return

		val file = File(filePath)
		if(!file.exists()) return

		Scanner(FileInputStream(file)).use {
			while(it.hasNextLine()){
				val line = it.nextLine() ?: break
				if(frameTimeRegex.matcher(line).matches()){
					val res = frameTimeRegex.matcher(line)
					res.find()
					frameTime = res.group(1).toFloat()
					System.out.println("FrameTime: " + frameTime)
					continue
				}
				if(frameRegex.matcher(line).matches()){
					val res = frameRegex.matcher(line)
					res.find()
					val tmpPath = res.group(1)
					numberOfFrames++
					framesList.add(Texture(file.parentFile.canonicalPath + File.separator + tmpPath))
					continue
				}
				if(repeatRegex.matcher(line).matches()){
					val res = repeatRegex.matcher(line)
					res.find()
					repeat = res.group(1).toBoolean()
					continue
				}
			}
		}


		for(t in framesList)
			t.load()

		loaded = true
	}
}