package com.jstefanelli.jfs3d.engine.utils.demo

import com.jstefanelli.jfs3d.engine.Config
import org.joml.Vector3f
import com.jstefanelli.jfs3d.engine.Texture
import com.jstefanelli.jfs3d.engine.utils.TextureFactory

class CubeDefinition(var texture: String, var position: Vector3f, var color: Vector3f, private val cfg: Config) {
	var textureObject: Texture? = null
	var loaded = false

	fun load(){
		if(loaded)
			return

		if(!texture.isEmpty() && texture.toLowerCase().trim() != "none"){
			textureObject = Texture.make(texture, cfg)
			textureObject?.load()
		}

		loaded = true
	}
}