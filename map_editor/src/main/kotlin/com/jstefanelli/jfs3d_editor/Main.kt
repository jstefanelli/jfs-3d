package com.jstefanelli.jfs3d_editor

import com.jstefanelli.jfs3d.engine.*
import com.jstefanelli.jfs3d.engine.ui1.UI1Utils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.util.nfd.NativeFileDialog

class Main(var baseProjectFilePath: String = "") {

	lateinit var mainWindow: EngineWindow
	lateinit var config: Config
	lateinit var uiUtils: UI1Utils
	var loaded = false

	fun load(){
		if(loaded)
			return

		if(!World.initShaders()){
			mainWindow.close()
			loaded = true
			return
		}

		mainWindow.fullscreen = config.fullscreen

		glEnable(GL_DEPTH_TEST)
		glDepthFunc(GL_LEQUAL)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		glViewport(0, 0, mainWindow.actualWidth, mainWindow.actualHeight)

		loaded = true
	}

	fun drawRoutine(){
		if(!loaded)
			load()

		if(World.texture == null)
			return


	}

	fun run(){
		mainWindow = EngineWindow("JFS3D-Editor")
		config = Config.LoadFromFile("editorConfig.xml") ?: return
		World.initialize()
		mainWindow.width = config.resolutionX
		mainWindow.height = config.resolutionY
		mainWindow.fullScreenResolutionX = config.fsResolutionX
		mainWindow.fullScreenResolutionY = config.fsResolutionY
		mainWindow.addDrawCb(object: DrawCallback {
			override fun resize() {
				World.resize()
				glViewport(0, 0, mainWindow.actualWidth, mainWindow.actualHeight)
			}

			override fun draw() {
				drawRoutine()
			}

		})
		var cfg = Config.default
		if(cfg == null) {
			cfg = Config()
			cfg.saveToFile(Config.defaultPath)
		}
		uiUtils = UI1Utils(mainWindow, cfg)

		mainWindow.run()
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>){
			if(args[1].toLowerCase() == "-p" && args.size >= 2){
				Main(args[2]).run()
			}else {
				Main().run()
			}
		}
	}
}