package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

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