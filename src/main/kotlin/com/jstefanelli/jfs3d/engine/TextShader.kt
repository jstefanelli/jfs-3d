package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL20.*

class TextShader {
	var programId: Int = 0
		get
		private set



	var aPosLoc: Int = 0
		get
		private set

	var aTxtLoc: Int = 0
		get
		private set

	var uMvpLoc: Int = 0
		get
		private set

	var uUvLoc: Int = 0
		get
		private set

	var uTxtLoc: Int = 0
		get
		private set

	var uTxtColLoc: Int = 0
		get
		private set



	var loaded = false
		get
		private set




	fun load(): Boolean{
		if(loaded)
			return true

		val vSource =
				"""
				#version 110

				uniform mat4 uMvp;
				uniform mat3 uUv;

				attribute vec3 aPos;
				attribute vec2 aTxt;

				varying vec2 vTxt;

				void main(){
					gl_Position = uMvp * vec4(aPos, 1.0);
					vTxt = (uUv * vec3(aTxt, 1.0)).xy;
				}

				"""

		val fSource =
				"""
				#version 110

				uniform sampler2D uTxt;
				uniform vec4 uTxtCol;

				varying vec2 vTxt;

				void main(){
					float font_alpha = texture2D(uTxt, vTxt).a;
					vec4 col = uTxtCol * font_alpha;
					if(col.a < 0.1)
						discard;
					gl_FragColor = col;
				}
				"""

		programId = compileShader(vSource, fSource) ?: return false

		aPosLoc = glGetAttribLocation(programId, "aPos")
		aTxtLoc = glGetAttribLocation(programId, "aTxt")

		uMvpLoc = glGetUniformLocation(programId, "uMvp")
		uUvLoc = glGetUniformLocation(programId, "uUv")

		uTxtLoc = glGetUniformLocation(programId, "uTxt")
		uTxtColLoc = glGetUniformLocation(programId, "uTxtCol")

		loaded = true
		return true
	}
}