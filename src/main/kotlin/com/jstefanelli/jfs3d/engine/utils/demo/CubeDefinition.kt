package com.jstefanelli.jfs3d.engine.utils.demo

import org.joml.Vector3f
import com.jstefanelli.jfs3d.engine.Texture

class CubeDefinition(var texture: String, var position: Vector3f, var color: Vector3f) {
	var textureObject: Texture? = null
	var loaded = false

	fun load(){
		if(loaded)
			return

		if(!texture.isEmpty() && texture.toLowerCase().trim() != "none"){
			textureObject = Texture(texture)
		}

		loaded = true
	}
}