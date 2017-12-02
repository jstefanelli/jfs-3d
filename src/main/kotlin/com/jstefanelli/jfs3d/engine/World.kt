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
		var fov: Float = 80.0f
		var currentWindow: EngineWindow? = null
		var cube: Cube? = null
		var texture: TextureShader? = null
		var color: ColorShader? = null
		var floor: StaticPlane? = null
		var projectionMatrix: Matrix4f = Matrix4f()
		var lookAtMatrix: Matrix4f = Matrix4f()
		var playerPosition: Vector3f = Vector3f()
			get
			set(value){
				field = value
				System.out.println("Update")
			}
		var playerRotation: Float = 0f

		fun initialize(){
			val w = currentWindow ?: return
			projectionMatrix.perspective(Mathf.toRadians(fov),w.width.toFloat() / w.height.toFloat(), 0.01f, 100f)
			lookAtMatrix.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))
			playerPosition = Vector3f(0f, 0f, 0f)
			playerRotation = 0f
			System.out.println("World initialized.")
		}

		fun movePlayer(movement: Vector3f, m: Map){
			System.out.println("Mov0: " + movement)
			movement.rotateAxis(playerRotation, 0f, -1f, 0f)

			System.out.println("Mov1: " + movement)
			playerPosition.add(movement)
		}

		fun movePlayer(x: Float, y: Float, z: Float, m: Map){
			movePlayer(Vector3f(x, y, z), m)
		}
	}
}