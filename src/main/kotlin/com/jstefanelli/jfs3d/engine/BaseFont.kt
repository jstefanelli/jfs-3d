package com.jstefanelli.jfs3d.engine

import org.joml.*
import org.lwjgl.*
import org.lwjgl.stb.*
import org.lwjgl.system.*


import java.io.*
import java.nio.*

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.stb.STBTruetype.*

class BaseFont (val filePath: String){
	private var ttf: ByteBuffer? = null
	private var info: STBTTFontinfo? = null


	private var ascent = 0
	private var descent = 0
	private var lineGap = 0
	private var textureId = 0

	private var buff: STBTTBakedChar.Buffer? = null

	var ATLAS_SIZE_X = 1024
	var ATLAS_SIZE_Y = 1024

	var fontHeight = 40.0f

	var loaded = false
		get
		private set

	init{
		try {
			ttf = Utils.ioResourceToByteBuffer(filePath, 512 * 1024)
		}catch(e: IOException){
			throw RuntimeException("Unable to load resource.", e)
		}

		info = STBTTFontinfo.create()
		if(!stbtt_InitFont(info, ttf)){
			throw IllegalStateException("Cannot init font")
		}

		MemoryStack.stackPush().use {
			val pAscent = it.mallocInt(1)
			val pDescent = it.mallocInt(1)
			val pLineGap = it.mallocInt(1)

			stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap)

			ascent = pAscent[0]
			descent = pDescent[0]
			lineGap = pLineGap[0]
		}
	}

	companion object{
		@JvmStatic
		val TAG = "BASEFONT"

		@JvmStatic
		private var staticLoaded = false

		private var vbo: Int = 0
		private var tbo: Int = 0
		private var mvp = BufferUtils.createFloatBuffer(16)
		private var uv = BufferUtils.createFloatBuffer(9)

		@JvmStatic
		fun staticLoad(){
			if(staticLoaded)
				return

			val shader = TextShader()
			if(!shader.load()){
				throw RuntimeException("Cannot compile text shader")
			}

			World.text = shader

			vbo = glGenBuffers()
			glBindBuffer(GL_ARRAY_BUFFER, vbo)
			glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
					0.0f, 0.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					1.0f, 0.0f, 0.0f,

					0.0f, 1.0f, 0.0f,
					1.0f, 1.0f, 0.0f,
					1.0f, 0.0f, 0.0f
			), GL_STATIC_DRAW)

			tbo = glGenBuffers()
			glBindBuffer(GL_ARRAY_BUFFER, tbo)
			glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
					1.0f, 0.0f,
					1.0f, 1.0f,
					0.0f, 0.0f,

					1.0f, 1.0f,
					0.0f, 1.0f,
					0.0f, 0.0f
			), GL_STATIC_DRAW)

			staticLoaded = true
		}
	}

	fun load(){
		if(loaded)
			return

		if(!staticLoaded)
			staticLoad()

		textureId = glGenTextures()
		val b = STBTTBakedChar.malloc(256)

		val bitmap = BufferUtils.createByteBuffer(ATLAS_SIZE_X * ATLAS_SIZE_Y)
		stbtt_BakeFontBitmap(ttf, fontHeight, bitmap, ATLAS_SIZE_X, ATLAS_SIZE_Y, 0, b)

		glBindTexture(GL_TEXTURE_2D, textureId)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, ATLAS_SIZE_X, ATLAS_SIZE_Y, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

		buff = b
		loaded = true
	}

	fun drawTextAt(text: String, pos: Vector2f, color: Vector4f, scale: Float = 1.0f){
		if(!loaded) {
			World.log.warn(TAG, "Loading font during draw. Please use the load() function at an appropriate time")
			load()
		}

		MemoryStack.stackPush().use {
			val sh = World.text?: return
			pos.y = -pos.y

			val pCodePoint: IntBuffer = it.mallocInt(1)

			val x: FloatBuffer = it.floats(0f)
			val y: FloatBuffer = it.floats(0f)

			val q: STBTTAlignedQuad = STBTTAlignedQuad.mallocStack(it)

			var lineStart: Int = 0

			var lineY = 0.0f

			var i: Int = 0
			var to: Int = text.length

			glUseProgram(sh.programId)

			glActiveTexture(GL_TEXTURE0)
			glBindTexture(GL_TEXTURE_2D, textureId)
			glUniform1i(sh.uTxtLoc, 0)

			glUniform4f(sh.uTxtColLoc, color.x, color.y, color.z, color.w)

			glEnableVertexAttribArray(sh.aPosLoc)
			glBindBuffer(GL_ARRAY_BUFFER, vbo)
			glVertexAttribPointer(sh.aPosLoc, 3, GL_FLOAT, false, 0, 0)

			glEnableVertexAttribArray(sh.aTxtLoc)
			glBindBuffer(GL_ARRAY_BUFFER, tbo)
			glVertexAttribPointer(sh.aTxtLoc, 2, GL_FLOAT, false, 0, 0)

			while(i  < to){
				i += parseCharacter(text, to, i, pCodePoint)

				val cp = pCodePoint[0]
				if(cp < 32 || cp >= 128)
					continue

				val cpx = x[0]
				stbtt_GetBakedQuad(buff, ATLAS_SIZE_X, ATLAS_SIZE_Y, cp, x, y, q, true)

				x.put(0, scale(cpx, x.get(0), 1.0f))

				var x0 = q.x1()
				var x1 = q.x0()
				var y0 = q.y0()
				var y1 = q.y1()

				val height = y1 - y0
				x0 *= scale
				x1 *= scale
				y0 *= scale
				y1 *= scale

				val drawPos = Vector2f(x0, (World.currentWindow?.height ?: return).toFloat() + y0)
				drawPos.add(pos)
				val drawScale = Vector2f((x1 - x0),(y1 - y0))

				makeMvpText(drawPos, mvp, 0, drawScale)

				val myUv = Matrix3f()

				myUv.scale((q.s1() - q.s0()), (q.t1() - q.t0()), 1f)
				myUv.m20 = q.s0()
				myUv.m21 = q.t0()
				myUv.get(uv)
				uv.position(0)

				glUniformMatrix4fv(sh.uMvpLoc, false, mvp)
				glUniformMatrix3fv(sh.uUvLoc, false, uv)

				glDrawArrays(GL_TRIANGLES, 0, 6)
			}

			glDisableVertexAttribArray(sh.aTxtLoc)
			glDisableVertexAttribArray(sh.aPosLoc)
		}
	}

	private fun parseCharacter(text: String, max: Int, i: Int, pCodePoint: IntBuffer): Int {
		val c1 = text.elementAt(i)
		if(c1.isHighSurrogate() && i + 1 < max){
			val c2 = text.elementAt(i + 2)
			if(c2.isLowSurrogate()){
				pCodePoint.put(0, Character.toCodePoint(c1, c2))
				return 2
			}
		}
		pCodePoint.put(0, c1.toInt())
		return 1
	}

	private fun scale(center: Float, offset: Float, factor: Float): Float {
		return (offset - center) * factor + center
	}
}