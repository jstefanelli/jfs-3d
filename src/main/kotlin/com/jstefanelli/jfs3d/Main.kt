package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.*
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*

class Main(){

	private var window: EngineWIndow? = null

	private var loaded = false

	private fun Load(){
		if(window == null)
			return

		glEnable(GL_DEPTH_TEST)
		glDepthFunc(GL_LEQUAL)

		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		World.currentWindow = window
		World.color = ColorShader()
		if(!World.color!!.compile()){
			window!!.close()
			return
		}

		World.floor = StaticPlane(Vector3f(0.5f, 0.5f, 0.6f))
		if(World.floor == null) {
			window!!.close()
			return
		}
		World.floor!!.load()

		loaded = true
	}

	fun drawGL(){
		if(!loaded)
			Load()

		World.floor?.draw()
	}

    init{
		window = EngineWIndow("JFS-3D")
	    window?.width = 1280
	    window?.height = 720
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
            val wind = Main()
        }
    }

}