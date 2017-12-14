package com.jstefanelli.jfs3d.engine.utils

import com.jstefanelli.jfs3d.engine.BaseFont
import com.jstefanelli.jfs3d.engine.World
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import com.jstefanelli.jfs3d.engine.*
import org.joml.Vector2f

class Console(var parser: CommandParser?, val font: BaseFont, var color: Vector4f = Vector4f(0.2f, 0.2f, 0.2f, 0.4f), var height: Float = 200.0f, var lineHeight: Float = 15.0f){

    companion object {
        @JvmStatic
        val TAG = "Utils/Console"

        @JvmStatic
        private var staticLoaded = false

        @JvmStatic
        private var vbo: Int = 0

        @JvmStatic
        private val mvp: FloatBuffer = BufferUtils.createFloatBuffer(16)

        @JvmStatic
        private val vp: Matrix4f = Matrix4f()

        @JvmStatic
        fun staticLoad(){
            if(staticLoaded)
                return

            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                    0f, 0f, 0f,
                    0f, 1f, 0f,
                    1f, 0f, 0f,

                    0f, 1f, 0f,
                    1f, 1f, 0f,
                    1f, 0f, 0f
            ), GL_STATIC_DRAW)

            staticLoaded = true
        }
    }

    private var currentCommand: String = ""
    private val commandHistory: ArrayList<String> = ArrayList()
    var loaded: Boolean = false
        get
        private set
    var shown: Boolean = false

    private fun runCommand(){
        World.log.log(TAG, "Sending console command: " + currentCommand)
        parser?.parseCommand(currentCommand)
        commandHistory.add(currentCommand)
        currentCommand = ""
    }

    fun parseKey(key: Int, scanCode: Int, action: Int, mode: Int){
        if(action == GLFW_PRESS || action == GLFW_REPEAT){
            val shift = mode.and(GLFW_MOD_SHIFT) != 0
            var k: Char = ' '
            if(key == GLFW_KEY_GRAVE_ACCENT){
                World.log.log(TAG, "Switching on/off")
                shown = !shown
                return
            }
            if(!shown){
                return
            }
            if(key == GLFW_KEY_ENTER){
                runCommand()
                return
            }
            if(key == GLFW_KEY_BACKSPACE){
                World.log.log(TAG, "BKSP")
                if(currentCommand.isNotEmpty())
                    currentCommand = currentCommand.substring(0 until currentCommand.length - 1)
                return
            }
            if(key == GLFW_KEY_TAB) {
                currentCommand += "    "
                return
            }
            if(key in 0..255){
                k = key.toChar()
                if(shift){
                    k = k.toUpperCase()
                }else{
                    k = k.toLowerCase()
                }
                if(k.isLetterOrDigit() || k.isWhitespace())
                    currentCommand += k
            }
        }
    }


    fun load(){
        if(!staticLoaded)
            staticLoad()

        if(!staticLoaded)
            throw RuntimeException("Static values not initialized. Error")

        if(!font.loaded)
            font.load()

        loaded = true
    }

    fun draw(){
        if(!shown)
            return

        if(!loaded || !staticLoaded) {
            World.log.warn(TAG, "Loading mid-render, This is not recommended")
            load()
        }

        makeMvpUi(Vector2f(0f, (World.currentWindow?.height ?: return).toFloat() - height), mvp, 0, Vector2f((World.currentWindow?.width ?: return).toFloat(), height))

        val p = World.color ?: return

        glUseProgram(p.programId)

        glUniformMatrix4fv(p.uMvpLoc, false, mvp)
        glUniform4f(p.uColLoc, color.x, color.y, color.z, color.w)

        glEnableVertexAttribArray(p.aPosLoc)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glVertexAttribPointer(p.aPosLoc, 3, GL_FLOAT, false, 0, 0)

        glDrawArrays(GL_TRIANGLES, 0, 6)

        glDisableVertexAttribArray(p.aPosLoc)

        font.drawTextAt(currentCommand, Vector2f(5f, (World.currentWindow?.height ?: return).toFloat() - height + 2f), Vector4f(1f, 1f, 1f, 1f), 0.2f)
    }
}

