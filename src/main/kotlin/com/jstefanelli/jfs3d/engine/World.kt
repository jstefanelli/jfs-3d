package com.jstefanelli.jfs3d.engine

import org.joml.Matrix4f
import org.joml.Quaterniond
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import com.jstefanelli.jfs3d.Map


class World(){
	companion object {
		@JvmStatic
		val TAG = "WORLD"

		@JvmStatic
		var fov: Float = 80.0f
		@JvmStatic
		var currentWindow: EngineWindow? = null
		@JvmStatic
		var cube: Cube? = null
		@JvmStatic
		var texture: TextureShader? = null
		@JvmStatic
		var color: ColorShader? = null
		@JvmStatic
		var floor: StaticPlane? = null
		@JvmStatic
		var text: TextShader? = null
		@JvmStatic
		var projectionMatrix: Matrix4f = Matrix4f()
		@JvmStatic
		var textProjectionMatrix: Matrix4f = Matrix4f()
		@JvmStatic
		var uiProjectionMatrix: Matrix4f = Matrix4f()
		@JvmStatic
		var lookAtMatrix: Matrix4f = Matrix4f()
		@JvmStatic
		var playerPosition: Vector3f = Vector3f()
		@JvmStatic
		var playerRotation: Float = 0f
		@JvmStatic
		var log = Log("log.txt")

		@JvmStatic
		fun initialize(){
			val w = currentWindow ?: return
			//log.doEcho = false
			resize()
			playerPosition = Vector3f(0f, 0f, 0f)
			playerRotation = 0f
			log.log(TAG, "World initialized.")
		}

		@Override
		fun resize(){
			val w = currentWindow ?: return
			projectionMatrix.identity()
			projectionMatrix.perspective(Mathf.toRadians(fov),w.actualWidth.toFloat() / w.actualHeight.toFloat(), 0.01f, 100f)
			textProjectionMatrix.identity()
			textProjectionMatrix.ortho2D(0f, w.actualWidth.toFloat(), w.actualHeight.toFloat(), 0f)
			uiProjectionMatrix.identity()
			uiProjectionMatrix.ortho2D(0f, w.actualWidth.toFloat(), 0f, w.actualHeight.toFloat())
			lookAtMatrix.identity()
			lookAtMatrix.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))
		}

		fun initShaders(): Boolean{
			val t = TextureShader()
			if(!t.load()) {
				log.err(TAG, "Failed to load texture shader")
				return false
			}
			texture = t

			val c = ColorShader()
			if(!c.compile()) {
				log.err(TAG, "Failed to load color shader")
				return false
			}
			color = c

			val te = TextShader()
			if(!te.load()) {
				log.err(TAG, "Failed to load text shader")
				return false
			}
			text = te
			return true
		}

		fun initCube(){
			val c = Cube()
			c.load()
			cube = c
		}
	}
}