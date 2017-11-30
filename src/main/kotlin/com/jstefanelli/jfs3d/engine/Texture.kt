package com.jstefanelli.jfs3d.engine

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer
import java.nio.ByteBuffer

class Texture(private val filePath: String){
    var textureId: Int = -1

    fun load(): Boolean{
        MemoryStack.stackPush().use{
            val w: IntBuffer = it.mallocInt(1)
            val h: IntBuffer = it.mallocInt(1)
            val comp: IntBuffer = it.mallocInt(1)
            stbi_set_flip_vertically_on_load(true)
            val image: ByteBuffer = stbi_load(filePath, w, h, comp, 4) ?: return false
            val width = w.get()
            val height = h.get()

            textureId = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)


            return true
        }
    }
}