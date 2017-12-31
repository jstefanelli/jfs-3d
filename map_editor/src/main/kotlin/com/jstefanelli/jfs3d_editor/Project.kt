package com.jstefanelli.jfs3d_editor

import java.io.File
class Project(var path: String) {

	val scenes = ArrayList<Scene>()
	val entityTypes = ArrayList<Entity>()
	val pickupTypes = ArrayList<Pickup>()
	val wallTypes = ArrayList<Wall>()

	fun exists(): Boolean{
		return File(path).exists()
	}

	private fun parse(){

	}

	init {
		if(exists()){
			parse()
		}
	}

}