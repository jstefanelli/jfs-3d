package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

class TextureShader{

    var programId: Int = 0
    var aPosLoc: Int = 0
    var aTxtLoc: Int = 0
    var uMvpLoc: Int = 0
    var uTxtLoc: Int = 0
    var uUvLoc: Int = 0

    fun load(): Boolean {
        val vSource = """
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

        val fSource = """
            #version 110

            uniform sampler2D uTxt;

            varying vec2 vTxt;

            void main(){
                vec4 col = texture2D(uTxt, vTxt);
                if(col.a < 0.1){
                    discard;
                }
                gl_FragColor = col;
            }

            """

        programId = compileShader(vSource, fSource) ?: return false

        aPosLoc = glGetAttribLocation(programId, "aPos")
        aTxtLoc = glGetAttribLocation(programId, "aTxt")

        uMvpLoc = glGetUniformLocation(programId, "uMvp")
        uUvLoc = glGetUniformLocation(programId, "uUv")
        uTxtLoc = glGetUniformLocation(programId, "uTxt")

        return true
    }
}