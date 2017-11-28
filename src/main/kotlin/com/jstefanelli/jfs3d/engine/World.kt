package com.jstefanelli.jfs3d.engine

class World(){
	companion object {
		var fov: Float = 80.0f
		var currentWindow: EngineWIndow? = null
		var color: ColorShader? = null
		var floor: StaticPlane? = null
	}
}