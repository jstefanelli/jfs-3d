package com.jstefanelli.jfs3d.engine

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import java.nio.FloatBuffer


val floatValuePattern = "([0-9.\\-f]+)"

fun makeMvp(position: Vector3f, orientation: Quaternionf, buffer: FloatBuffer, index: Int, scale: Vector3f = Vector3f(1f, 1f, 1f)){
    val model = Matrix4f()
    val invertedPos = Vector3f(World.playerPosition)
    model.rotate(World.playerRotation, Vector3f(0f, 1f, 0f))
    model.translate(invertedPos.mul(-1f))
    model.translate(position)
	model.scale(scale)
	model.rotate(orientation)

    val mvpMat = Matrix4f()
    World.lookAtMatrix.mul(model, mvpMat)
    World.projectionMatrix.mul(mvpMat, mvpMat)

    mvpMat.get(buffer)
    buffer.position(index)
}

fun makeMvpText(position: Vector2f, buffer: FloatBuffer, index: Int, scale: Vector2f = Vector2f(1.0f, 1.0f)){
    val model = Matrix4f()
	model.translate(position.x, position.y, 0f)
    model.scale(scale.x, scale.y, 1f)

    val myMat = Matrix4f()
    World.lookAtMatrix.mul(model, myMat)
    World.textProjectionMatrix.mul(myMat, myMat)

    myMat.get(buffer)
    buffer.position(index)
}

fun printGlError(msg: String? = null){
    val err = glGetError()

    val from: String = if(msg == null) "" else " from: " + msg

    when (err){
        GL_NO_ERROR -> {
            return
        }
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
        GL30.GL_INVALID_FRAMEBUFFER_OPERATION -> {
            System.err.println("GL/Error INVALID_FRAMEBUFFER_OPERATION" + from)
        }
    }
}


fun compileShader(vShaderSource: String, fShaderSource: String): Int?{
    val vShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vShader, vShaderSource)
    glCompileShader(vShader)
    if(glGetShaderi(vShader, GL_COMPILE_STATUS) == GL_FALSE){
        System.err.println("Vertex shader error: " + glGetShaderInfoLog(vShader))
        return null
    }

    val fShader = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fShader, fShaderSource)
    glCompileShader(fShader)
    if(glGetShaderi(fShader, GL_COMPILE_STATUS) == GL_FALSE){
        System.err.println("Fragment shader error: " + glGetShaderInfoLog(fShader))
        return null
    }

    val programId = glCreateProgram()
    glAttachShader(programId, vShader)
    glAttachShader(programId, fShader)
    glLinkProgram(programId)
    if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE){
        System.err.println("Program link failed: " + glGetProgramInfoLog(programId))
        return null
    }
    return programId
}