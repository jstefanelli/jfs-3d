package com.jstefanelli.jfs3d.engine.ui1

import com.jstefanelli.jfs3d.engine.Config
import com.jstefanelli.jfs3d.engine.DrawCallback
import com.jstefanelli.jfs3d.engine.EngineWindow
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import java.nio.FloatBuffer


class UI1Utils(val window: EngineWindow, private val cfg: Config): DrawCallback{

	private var width = -1
	private var height = -1
	private var orthoMatrix = Matrix4f()
	var render: UI1Render
		private set

	init{
		window.addDrawCb(this)
		render = UI1Render(this, cfg)
		resize()
	}

	override fun draw() {

	}

	override fun resize() {
		width = window.actualWidth
		height = window.actualHeight

		synchronized(orthoMatrix) {
			orthoMatrix.identity()
			orthoMatrix.ortho2D(0f, width.toFloat(), 0f, height.toFloat())
		}
	}

	fun makeMvp(posPx: Vector2f, sizePx: Vector2f, buffer: FloatBuffer, bufferPosition: Int){
		val model = Matrix4f()
		model.translate(Vector3f(posPx, 0f))
		model.scale(Vector3f(sizePx, 1f))

		val myMat = Matrix4f()
		synchronized(orthoMatrix) {
			orthoMatrix.mul(model, myMat)
		}

		buffer.position(bufferPosition)
		myMat.get(buffer)
		buffer.position(bufferPosition)

	}
}