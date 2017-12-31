package com.jstefanelli.jfs3d.engine.utils

import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.World
import java.io.File

class TextureFactory private constructor() {
	companion object {
		@JvmStatic
		val TAG = "Utils/TextureFactory"

		private val textures: HashMap<String, Texture> = HashMap()

		fun exists(path: String): Boolean{
			return textures.containsKey(File(path).canonicalPath)
		}

		fun getTexture(path: String): Texture? {
			val file = File(path).canonicalFile
			if(textures.containsKey(file.canonicalPath)){
				return textures[file.canonicalPath]
			}
			if(!file.exists()){
				World.log.err(TAG, "Cannot load texture: File not found")
				return null
			}
			val t = Texture.make(file.canonicalPath) ?: return null
			textures.put(file.canonicalPath, t)
			return t
		}
	}

}