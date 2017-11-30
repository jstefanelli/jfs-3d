	package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.*
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

		System.out.println("Write map: ")

		val f = File("map.jmp")

        map = if(f.exists()) {
            Map(FileInputStream(f))
        }else {
            Map(System.`in`, true)
        }

		map?.parse()
		glEnable(GL_DEPTH_TEST)
		glDepthFunc(GL_LEQUAL)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		glViewport(0, 0, window?.width ?: 0, window?.height ?: 0)

		World.currentWindow = window
		World.color = ColorShader()
		if(!World.color!!.compile()){
			window!!.close()
			return
		}

		World.floor = StaticPlane()
		if(World.floor == null) {
			window!!.close()
			return
		}
		World.floor!!.load()

		World.cube = Cube()
		val cube: Cube = World.cube ?: return
		cube.load()

		loaded = true
	}

	fun drawGL(){
		if(!loaded)
			Load()

		map?.drawMap()

        if(lastTime == 0L)
            lastTime = System.currentTimeMillis()

        val time = System.currentTimeMillis()
        if(time - lastTime < 1000L){
            frames++
        }else{
            System.out.println("FPS: " + frames)
            frames = 0
            lastTime = time
        }
	}

    init{
		window = EngineWindow("JFS-3D")
	    window?.width = 640
	    window?.height = 480
	    window?.make()

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
    }

}