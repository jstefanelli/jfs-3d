package com.jstefanelli.jfs3d

import org.lwjgl.*
import org.lwjgl.glfw.*
import org.lwjgl.opengl.*
import org.lwjgl.system.*

import java.nio.*
import org.joml.*

import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*

class Main(){
    private var window: Long
    private var program: Int
    private var uMvpLoc: Int = 0
    private var aPosLoc: Int = 0
    private var aColLoc: Int = 0
    private var vBuff: Int = 0
    private var cBuff: Int = 0

    private fun makeShaders(): Boolean{
        val shader0 = """
            #version 120

            attribute vec3 aPos;
            attribute vec4 aCol;

            uniform mat4 uMvp;

            varying vec4 vCol;

            void main(){
                gl_Position = uMvp * vec4(aPos, 1.0);
                vCol = aCol;
            }

            """
        val shader1 = """
            #version 120

            veryng vec4 vCol;

            void main(){
                gl_FragColor = vCol;
            }
            """

        val vShader = glCreateShader(GL_VERTEX_SHADER)
        val fShader = glCreateShader(GL_FRAGMENT_SHADER)
        program = glCreateProgram()

        glShaderSource(vShader, shader0)
        glCompileShader(vShader)
        val vertexs = glGetShaderi(vShader, GL_COMPILE_STATUS)
        if(vertexs != GL_NO_ERROR){
            System.err.println("Vertex Shader error: " + glGetShaderInfoLog(vShader))
            return false
        }

        glShaderSource(fShader, shader1)
        glCompileShader(fShader)
        val fragments = glGetShaderi(fShader, GL_COMPILE_STATUS)
        if(fragments != GL_NO_ERROR){
            System.err.println("Fragment Shader error: " + glGetShaderInfoLog(fShader))
            return false
        }

        glAttachShader(program, vShader)
        glAttachShader(program, fShader)
        glLinkProgram(program)
        val programs = glGetProgrami(program, GL_LINK_STATUS)

        if(programs != GL_NO_ERROR){
            System.err.println("Link error: " + glGetProgramInfoLog(program))
            return false
        }

        uMvpLoc = glGetUniformLocation(program, "uMvp")
        aPosLoc = glGetAttribLocation(program, "aPos")
        aColLoc = glGetAttribLocation(program, "aCol")

        return true
    }

    private fun makeBuffers(){
        vBuff = glGenBuffers()

        val vBuffData: FloatArray = floatArrayOf(
                -0.5f, -0.5f, -0.5f,
                0f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f
        )

        glBindBuffer(GL_ARRAY_BUFFER, vBuff)
        glBufferData(GL_ARRAY_BUFFER, vBuffData, GL_STATIC_DRAW)

        cBuff = glGenBuffers()

        val cBufferData: FloatArray = floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        )

        glBindBuffer(GL_ARRAY_BUFFER, cBuff)
        glBufferData(GL_ARRAY_BUFFER, cBufferData, GL_STATIC_DRAW)

    }

    init{
        window = 0
        program = 0
        GLFWErrorCallback.createPrint(System.err).set()

        if(!glfwInit())
            throw IllegalStateException("GLFW Init failed")

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        window = glfwCreateWindow(800, 600, "SaS", NULL, NULL)
        if(window == NULL)
            throw RuntimeException("Failed to create window")

        glfwMakeContextCurrent(window)

        glfwSwapInterval(1)

        glfwShowWindow(window)

        loop()
    }

    private fun loop(){
        GL.createCapabilities()

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        if(!makeShaders()){
            glfwDestroyWindow(window)
            return
        }

        makeBuffers()

        var mat = Matrix4f()
        mat.perspective(90.0f, 4.0f / 3.0f, 0.1f, 10f)

        var view = Matrix4f()
        view.lookAt(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 0.0f, -1.0f), Vector3f(0.0f, 1.0f, 0.0f))

        mat.mul(view)

        glUseProgram(program)
        var f = BufferUtils.createFloatBuffer(16)
        mat.get(f)
        f.position(0)

        glUniformMatrix4fv(uMvpLoc, false, f)


        System.out.println("Ready to render")

        while (!glfwWindowShouldClose(window)){
            glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))


            glEnableVertexAttribArray(aPosLoc)
            glEnableVertexAttribArray(aColLoc)

            glBindBuffer(GL_ARRAY_BUFFER, vBuff)
            glVertexAttribPointer(aPosLoc, 3, GL_FLOAT, false, 0, 0)

            glBindBuffer(GL_ARRAY_BUFFER, cBuff)
            glVertexAttribPointer(aColLoc, 4, GL_FLOAT, false, 0, 0)

            glDrawArrays(GL_TRIANGLES, 0, 3)

            glDisableVertexAttribArray(aPosLoc)
            glDisableVertexAttribArray(aColLoc)

            glfwSwapBuffers(window)
        }

        glfwDestroyWindow(window)
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>)
        {
            val wind = Main()
        }
    }

}