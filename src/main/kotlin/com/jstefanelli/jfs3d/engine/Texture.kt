package com.jstefanelli.jfs3d.engine

import com.jstefanelli.jfs3d.engine.utils.TextureFactory
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.opengl.EXTTextureFilterAnisotropic.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer
import java.nio.ByteBuffer

class Texture private constructor(private val filePath: String, private val config: Config){

    companion object {
        @JvmStatic
	    val TAG = "TEXTURE"

        @JvmStatic
        fun make(path: String, config: Config): Texture?{
	        return if(!TextureFactory.exists(path)) {
		        Texture(path, config)
	        }else{
		        TextureFactory.getTexture(path, config)
	        }
        }

	    var anisotropySupported: Boolean? = null
	        private set
	    private var anisotropyExtensionName: String = "GL_EXT_texture_filter_anisotropic"
	    var maxAnisotropyLevel: Float = 0f
	        private set

	    fun requestAnisotropySupport(){
		    if(anisotropySupported != null)
			   return

			anisotropySupported = EngineWindow.queryExtension(anisotropyExtensionName)
		    if(anisotropySupported == true){
			    World.log.log(TAG, "Anisotropic filtering supported")
				maxAnisotropyLevel = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT)
			    World.log.log(TAG, "Max anisotropic filtering level: " + maxAnisotropyLevel)
		    }
	    }

    }

    var textureId: Int = -1

    var doAnisotropy: Boolean = false

    var loaded = false

    fun load(): Boolean{
        if(loaded)
            return true

        MemoryStack.stackPush().use{
            val w: IntBuffer = it.mallocInt(1)
            val h: IntBuffer = it.mallocInt(1)
            val comp: IntBuffer = it.mallocInt(1)
            stbi_set_flip_vertically_on_load(true)
            val image: ByteBuffer = stbi_load(filePath, w, h, comp, 4) ?: return false
			World.log.log(TAG, "Loading texture of size: " + w[0] + " " + h[0])
            val width = w.get()
            val height = h.get()

	        if(doAnisotropy){
		        requestAnisotropySupport()

		        var aniso : Boolean = anisotropySupported ?: false
				if(!aniso)
					doAnisotropy = false
	        }

            textureId = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
	        if(!doAnisotropy) {
		        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
		        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
	        }else{
		        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
		        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR)
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, Mathf.min(config.anisotropyLevel, maxAnisotropyLevel))
	        }
            glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)

            loaded = true

            return true
        }
    }
}