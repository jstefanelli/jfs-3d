package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*

fun printGlError(msg: String? = null){
	val err = glGetError()
	val from: String = if(msg == null) "" else " from: " + msg

	when (err){
		GL_INVALID_ENUM -> {
			System.err.println("GL/Error INVALID_ENUM" + from)
		}
		GL_INVALID_VALUE -> {
			System.err.println("GL/Error INVALID_VALUE" + from)
		}
		GL_INVALID_OPERATION -> {
			System.err.println("GL/Error INVALID_OPERATION" + from)
		}
		GL_STACK_OVERFLOW -> {
			System.err.println("GL/Error STACK_OVERFLOW" + from)
		}
		GL_STACK_UNDERFLOW -> {
			System.err.println("GL/Error STACK_UNDERFLOW" + from)
		}
		GL_OUT_OF_MEMORY -> {
			System.err.println("GL/Error OUT_OF_MEMORY" + from)
		}
		GL_INVALID_FRAMEBUFFER_OPERATION -> {
			System.err.println("GL/Error INVALID_FRAMEBUFFER_OPERATION" + from)
		}
	}
}

class World(){
	companion object {
		var fov: Float = 80.0f
		var currentWindow: EngineWindow? = null
		var cube: Cube? = null
		var texture: TextureShader? = null
		var color: ColorShader? = null
		var floor: StaticPlane? = null



	}
}