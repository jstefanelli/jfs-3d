package com.jstefanelli.jfs3d_editor

import com.jstefanelli.jfs3d.engine.*

class Main(var baseProjectFilePath: String = "NONE") {

	lateinit var mainWindow: EngineWindow
	lateinit var config: Config

	fun run(){
		mainWindow = EngineWindow("JFS3D-Editor")
		config = Config.LoadFromFile("editorConfig.xml") ?: return
		World.initialize()

	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>){
			Main().run()
		}
	}
}