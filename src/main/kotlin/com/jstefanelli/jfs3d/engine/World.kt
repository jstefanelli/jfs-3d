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
			projectionMatrix.perspective(Mathf.toRadians(fov),w.width.toFloat() / w.height.toFloat(), 0.01f, 100f)
			textProjectionMatrix.ortho2D(0f, w.width.toFloat(), w.height.toFloat(), 0f)
			uiProjectionMatrix.ortho2D(0f, w.width.toFloat(), 0f, w.height.toFloat())
			lookAtMatrix.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))
			playerPosition = Vector3f(0f, 0f, 0f)
			playerRotation = 0f
			log.log(TAG, "World initialized.")
		}

		@JvmStatic
		fun movePlayer(movement: Vector3f, m: Map){
			movement.rotateAxis(playerRotation, 0f, -1f, 0f)
			val tmp = Vector3f(playerPosition)
			tmp.add(movement)
			if(m.validateMovement(tmp, true)) playerPosition = tmp
		}

		@JvmStatic
		fun movePlayer(x: Float, y: Float, z: Float, m: Map){
			movePlayer(Vector3f(x, y, z), m)
		}
	}
}