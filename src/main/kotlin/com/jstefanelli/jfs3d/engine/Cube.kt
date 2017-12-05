package com.jstefanelli.jfs3d.engine

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer

class Cube{
    var loaded: Boolean = false
        get
        private set

    private var vbo: Int = 0
    private var tbo: Int = 0
    private var view: Matrix4f = Matrix4f()
    private var projection: Matrix4f = Matrix4f()

    private val fb = BufferUtils.createFloatBuffer(16)
    private val fbUv = BufferUtils.createFloatBuffer(9)

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
                .5f, -.5f, -.5f,

                //Top back left
                -.5f, .5f, .5f,
                -.5f, .5f, -.5f,
                .5f, .5f, .5f,

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
                1f, 0f,

                //Front bottom right
                1f, 0f,
                0f, 0f,
                0f, 1f,

                //Front top left
                1f, 0f,
                0f, 1f,
                1f, 1f,

                //Left front bottom
                0f, 0f,
                0f, 1f,
                1f, 0f,

                //Left back top
                0f, 1f,
                1f, 1f,
                1f, 0f,

                //Right back bottom
                0f, 0f,
                0f, 1f,
                1f, 0f,

                //Right front top
                0f, 1f,
                1f, 1f,
                1f, 0f,

                //Top back left
                0f, 0f,
                0f, 1f,
                1f, 0f,

                //Top front right
                0f, 1f,
                1f, 1f,
                1f, 0f,

                //Bottom back right
                1f, 1f,
                0f, 1f,
                0f, 0f,

                //Bottom front left
                1f, 1f,
                0f, 0f,
                1f, 0f

                ), GL_STATIC_DRAW)

        loaded = true


        view = Matrix4f()
        view.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))
        projection = Matrix4f()
        projection.perspective(Mathf.toRadians(World.fov), World.currentWindow!!.width.toFloat() / World.currentWindow!!.height.toFloat(), 0.01f, 100f)

    }

    private fun makeModelMatrix(pos: Vector3f, rotation: Quaternionf){
        fb.position(0)
        makeMvp(pos, rotation, fb, 0)

        val uv = Matrix3f()
        uv.identity()
        uv.get(fbUv)
        fbUv.position(0)
    }

    fun drawColorAt(pos: Vector3f, col: Vector4f, rotation: Quaternionf = Quaternionf()){
        val c = World.color ?: return

        makeModelMatrix(pos, rotation)

        glUseProgram(c.programId)
        glUniform4f(c.uColLoc, col.x, col.y, col.z, col.w)
        glUniformMatrix4fv(c.uMvpLoc, false, fb)


        glEnableVertexAttribArray(c.aPosLoc)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glVertexAttribPointer(c.aPosLoc, 3, GL_FLOAT, false, 0, 0)

        glDrawArrays(GL_TRIANGLES, 0, 36)

        glDisableVertexAttribArray(c.aPosLoc)
    }

    fun drawTextureAt(pos: Vector3f, txt: Int, rotation: Quaternionf = Quaternionf()){
        val t = World.texture ?: return

        makeModelMatrix(pos, rotation)
        glUseProgram(t.programId)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, txt)
        glUniform1i(t.uTxtLoc, 0)

        glUniformMatrix4fv(t.uMvpLoc, false, fb)
        glUniformMatrix3fv(t.uUvLoc, false, fbUv)

        glEnableVertexAttribArray(t.aPosLoc)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glVertexAttribPointer(t.aPosLoc, 3, GL_FLOAT, false, 0, 0)

        glEnableVertexAttribArray(t.aTxtLoc)
        glBindBuffer(GL_ARRAY_BUFFER, tbo)
        glVertexAttribPointer(t.aTxtLoc, 2, GL_FLOAT, false, 0, 0)

        glDrawArrays(GL_TRIANGLES, 0, 36)

        glDisableVertexAttribArray(t.aTxtLoc)
        glDisableVertexAttribArray(t.aPosLoc)
    }
}
