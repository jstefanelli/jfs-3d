package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.DrawCallback
import com.jstefanelli.jfs3d.engine.EngineWIndow

class Main(){

	private var window: EngineWIndow? = null

	fun drawGL(){

	}

    init{
		window = EngineWIndow("JFS-3D")
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