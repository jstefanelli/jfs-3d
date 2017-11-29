package com.jstefanelli.jfs3d.engine

import org.joml.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*

class Cube{
    private var loaded: Boolean = false

    private var vbo: Int = 0
    private var tbo: Int = 0

    fun load(){
        if(loaded)
            return

        vbo = glGenBuffers()
        tbo = glGenBuffers()

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                //Back bottom left
                -.5f, -.5f, .5f,
                -.5f, .5f, .5f,
                .5f, -.5f, .5f,

                //Back top right
                -.5f, .5f, .5f,
                .5f, .5f, .5f,
                .5f, -.5f, .5f,

                //Front bottom right
                -.5f, -.5f, -.5f,
                .5f, -.5f, -.5f,
                .5f, .5f, -.5f,

                //Front top left
                -.5f, -.5f, -.5f,
                .5f, .5f, -.5f,
                -.5f, .5f, -.5f,

                //Left front bottom
                -.5f, -.5f, -.5f,
                -.5f, .5f, -.5f,
                -.5f, -.5f, .5f,

                //Left back top
                -.5f, .5f, -.5f,
                -.5f, .5f, .5f,
                -.5f, -.5f, .5f,

                //Right back bottom
                .5f, -.5f, .5f,
                .5f, .5f, .5f,
                .5f, -.5f, -.5f,

                //Right front top
                .5f, .5f, .5f,
                .5f, .5f, -.5f,
                .5f, -.5f, -.5f

                //Top back left
                -.5f, .5f, .5f,
                -.5f, .5f, -.5f,
                .5f, .5f, -5f,

                //Top front right
                -.5f, .5f, -.5f,
                .5f, .5f, -.5f,
                .5f, .5f, .5f,

                //Bottom back right
                -.5f, -.5f, .5f,
                .5f, -.5f, .5f,
                .5f, -.5f, -.5f,

                //Bottom front left
                -.5f, -.5f, .5f,
                .5f, -.5f, -.5f,
                -.5f, -.5f, -.5f
        ), GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, tbo)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                //Back bottom left
                0f, 0f,
                0f, 1f,
                1f, 0f,

                //Back top right
                0f, 1f,
                1f, 1f,
                1f, 0f

                //TODO: Complete texture buffer
        ), GL_STATIC_DRAW)

        loaded = true
    }

    fun drawColorAt(pos: Vector3f, col: Vector4f){

    }

    fun drawTextureAt(pos: Vector3f, txt: Int){
        
    }
}
