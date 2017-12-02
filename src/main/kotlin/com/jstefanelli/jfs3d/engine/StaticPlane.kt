package com.jstefanelli.jfs3d.engine

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer

class StaticPlane(){

	var loaded: Boolean = false

	private var vbo: Int = 0
	private var mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)

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

		p.perspective(Mathf.toRadians(World.fov), World.currentWindow!!.width.toFloat() / World.currentWindow!!.height.toFloat(), 0.01f, 100f)
		v.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))

	}

	fun drawAt(position: Vector3f, color: Vector4f){
		val c: ColorShader = World.color ?: return


        val m = Matrix4f()

        m.scale(200f, 1f, 200f)
        m.translate(position)

        val mvpMat = Matrix4f()
        m.mul(v, mvpMat)
        p.mul(mvpMat, mvpMat)

        mvpMat.get(mvp)
        mvp.position(0)

        glUseProgram(c.programId)
		glUniformMatrix4fv(c.uMvpLoc, false, mvp)
		glUniform4f(c.uColLoc, color.x, color.y, color.z, 1.0f)

		glEnableVertexAttribArray(c.aPosLoc)

		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glVertexAttribPointer(c.aPosLoc, 3, GL_FLOAT, false, 0, 0)

		glDrawArrays(GL_TRIANGLES, 0, 6)

		glDisableVertexAttribArray(c.aPosLoc)
	}
}