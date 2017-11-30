package com.jstefanelli.jfs3d.engine

class World(){
	companion object {
		var fov: Float = 80.0f
		var currentWindow: EngineWindow? = null
		var cube: Cube? = null
		var texture: TextureShader? = null
		var color: ColorShader? = null
		var floor: StaticPlane? = null
	}
}