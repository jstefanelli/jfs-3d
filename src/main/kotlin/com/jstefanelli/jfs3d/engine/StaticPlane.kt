package com.jstefanelli.jfs3d.engine

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer

class StaticPlane(){

	var loaded: Boolean = false

	private var vbo: Int = 0
	private var tbo: Int = 0
	private var mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)
	private var uv: FloatBuffer = BufferUtils.createFloatBuffer(9)

	private var p: Matrix4f = Matrix4f()
	private var v: Matrix4f = Matrix4f()

	fun load(){
		if(loaded)
			return

		vbo = glGenBuffers()
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
				-.5f, 0f, .5f,
				-.5f, 0f, -.5f,
				.5f, 0f, .5f,

				-.5f, 0f, -.5f,
				.5f, 0f, -.5f,
				.5f, 0f, .5f
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

		p.perspective(Mathf.toRadians(World.fov), World.currentWindow!!.width.toFloat() / World.currentWindow!!.height.toFloat(), 0.01f, 100f)
		v.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))
		val uvMat = Matrix3f()
		uvMat.scale(Vector3f(300f, 300f, 1f))

		uvMat.get(uv)
		uv.position(0)
	}

	fun drawAt(position: Vector3f, color: Vector4f){
		val c: ColorShader = World.color ?: return

		makeMvp(position, Quaternionf(),  mvp, 0, Vector3f(300f, 1f, 300f))

        glUseProgram(c.programId)
		glUniformMatrix4fv(c.uMvpLoc, false, mvp)
		glUniform4f(c.uColLoc, color.x, color.y, color.z, 1.0f)

		glEnableVertexAttribArray(c.aPosLoc)

		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glVertexAttribPointer(c.aPosLoc, 3, GL_FLOAT, false, 0, 0)

		glDrawArrays(GL_TRIANGLES, 0, 6)

		glDisableVertexAttribArray(c.aPosLoc)
	}

	fun drawAt(position: Vector3f, textureId: Int){
		val t: TextureShader = World.texture ?: return

		makeMvp(position, Quaternionf(), mvp, 0, Vector3f(300f, 1f, 300f))

		glUseProgram(t.programId)
		glUniformMatrix3fv(t.uUvLoc, false, uv)
		glUniformMatrix4fv(t.uMvpLoc, false, mvp)
		glUniform1i(t.uTxtLoc, 0)
		glActiveTexture(GL_TEXTURE0)
		glBindTexture(GL_TEXTURE_2D, textureId)

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