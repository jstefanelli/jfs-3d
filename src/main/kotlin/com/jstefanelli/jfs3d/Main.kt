package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.*
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11.*
import java.io.File
import java.io.FileInputStream

class Main(){

	private var window: EngineWindow? = null

	private var loaded = false

	private var lastTime: Long = 0
	private var frames: Int = 0
	private var map: Map? = null

	private fun Load(){
		if(window == null)
			return

		if(loaded)
			return

        World.currentWindow = window

		val f = File("map.jmp")

        map = if(f.exists()) {
            Map(FileInputStream(f))
        }else {
            Map(System.`in`, true)
        }

		World.log.log(TAG, "Load start")

		World.initialize()

		map?.parse()
		glEnable(GL_DEPTH_TEST)
		glDepthFunc(GL_LEQUAL)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		glViewport(0, 0, window?.width ?: 0, window?.height ?: 0)

		World.color = ColorShader()
		if(!World.color!!.compile()){
			window!!.close()
			return
		}

		val t = TextureShader()
		if(!t.load()){
			window!!.close()
			return
        }
		World.texture = t

		World.floor = StaticPlane()
		if(World.floor == null) {
			window!!.close()
			return
		}
		World.floor!!.load()

		World.cube = Cube()
		val cube: Cube = World.cube ?: return
		cube.load()

		JSpriteType.JSpriteInstance.initialize()

		World.playerPosition = Vector3f(0f, 0f, -1f)

		loaded = true
		World.log.log(TAG,"Load over")
	}

	fun drawGL(){
		if(!loaded)
			Load()

        val time = System.currentTimeMillis()

		map?.updateMap()

		map?.drawMap()

		glClear(GL_DEPTH_BUFFER_BIT)

        if(lastTime == 0L)
            lastTime = System.currentTimeMillis()

        if(time - lastTime < 1000L){
            frames++
        }else{
            World.log.log(TAG, "FPS: " + frames)
            frames = 0
            lastTime = time
        }
	}

    init{
		window = EngineWindow("JFS-3D")
		window?.doVsync = true
	    window?.width = 1280
	    window?.height = 720
	    window?.make()

		window?.addKeyCb(object: GLFWKeyCallback() {
            override fun invoke(windowLong: Long, key: Int, scancode: Int, action: Int, mods: Int) {
				if(action == GLFW_PRESS ){
					when(key){
						GLFW_KEY_ESCAPE -> {
							window?.close()
						}
						GLFW_KEY_ENTER -> {
							if(mods.and(GLFW_MOD_ALT) > 0){
								window?.fullscreen = !(window?.fullscreen ?: return)
							}
						}
					}
                }
            }

        })

	    window?.drawCb = object: DrawCallback {
		    override fun draw() {
				drawGL()
		    }
	    }

		window?.run()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>)
        {
            Main()
        }

	    @JvmStatic
	    val TAG = "MAIN"
    }

}