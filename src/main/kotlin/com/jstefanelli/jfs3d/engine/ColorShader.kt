package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

class ColorShader{

	private var loaded = false

	var programId: Int = 0
		private set

	var aPosLoc: Int = 0
	var uMvpLoc: Int = 0
	var uColLoc: Int = 0

	fun compile() : Boolean{
		if(loaded)
			return true
		val vSource = """
			#version 110

			uniform mat4 uMvp;

			attribute vec3 aPos;

			void main(){
				gl_Position = uMvp * vec4(aPos, 1.0);
			}

			"""

		val fSource = """
			#version 110

			uniform vec4 uCol;

			void main(){
				gl_FragColor = uCol;
			}

			"""

		programId = compileShader(vSource, fSource) ?: return false

		aPosLoc = glGetAttribLocation(programId, "aPos")
		uMvpLoc = glGetUniformLocation(programId, "uMvp")
		uColLoc = glGetUniformLocation(programId, "uCol")

		loaded = true

		return true
	}


}