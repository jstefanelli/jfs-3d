package com.jstefanelli.jfs3d.engine.utils

import com.jstefanelli.jfs3d.engine.Cube
import com.jstefanelli.jfs3d.engine.Entity
import com.jstefanelli.jfs3d.engine.utils.demo.*

class DemoRecorder {

	val steps: ArrayList<Step> = ArrayList()
	val objects: ArrayList<Object> = ArrayList()
	val cubes: ArrayList<CubeDefinition> = ArrayList()

	fun addStep(step: Step){
		synchronized(steps){
			steps.add(step)
		}
	}

	fun addObjects(obj: Entity.EntityInstance, visible: Boolean){
		synchronized(objects){
			objects.add(Object(obj, visible))
		}
	}

	fun addCube(cube: CubeDefinition){
		synchronized(cubes){
			cubes.add(cube)
		}
	}

}