package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.*
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11.*
import java.io.File
import java.io.FileInputStream

class Main(){

	private var window: EngineWindow? = null

	private var loaded = false

	private var movementSpeed = 1.8f
	private var rotateSensitivity = 3.5f

	private var lastTime: Long = 0
	private var frames: Int = 0
	private var map: Map? = null
	private var explosion: JSpriteType? = null
	private var explosions: ArrayList<Pair<Vector3f, JSpriteType.JSpriteInstance>> = ArrayList()

	private fun Load(){
		if(window == null)
			return

		if(loaded)
			return

        World.currentWindow = window

		System.out.println("Write map: ")

		val f = File("map.jmp")

        map = if(f.exists()) {
            Map(FileInputStream(f))
        }else {
            Map(System.`in`, true)
        }

		System.out.println("Load start")

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

		explosion = JSpriteType("textures/frames/explosion.jfr")
		explosion?.load()

		World.playerPosition = Vector3f(0f, 0f, -1f)

		loaded = true
		System.out.println("Load over")
	}

	fun drawGL(){
		if(!loaded)
			Load()
		if((window?.window ?: return) == 0L) return
		if(glfwGetKey(window?.window ?: return, GLFW_KEY_A) == GLFW_PRESS){
			World.playerRotation -= 0.01f * rotateSensitivity
        }
		if(glfwGetKey(window?.window ?: return, GLFW_KEY_D) == GLFW_PRESS){
			World.playerRotation += 0.01f * rotateSensitivity
        }
		if(glfwGetKey(window?.window ?: return, GLFW_KEY_W) == GLFW_PRESS){
			World.movePlayer(0f, 0f, -0.02f * movementSpeed, map ?: return)
        }
		if(glfwGetKey(window?.window ?: return, GLFW_KEY_S) == GLFW_PRESS){
			World.movePlayer(0f, 0f, +0.02f * movementSpeed, map ?: return)
        }

		map?.drawMap()

		glClear(GL_DEPTH_BUFFER_BIT)

		synchronized(explosions){
			for(i in explosions){
				i.second.drawAt(i.first)
			}
			var i = 0
			while(true){
				if(i >= explosions.count())
					break
				val t = explosions[i]
				if(!t.second.running)
					explosions.removeAt(i)
				else
					i++
			}
		}

        if(lastTime == 0L)
            lastTime = System.currentTimeMillis()

        val time = System.currentTimeMillis()
        if(time - lastTime < 1000L){
            frames++
        }else{
            //System.out.println("FPS: " + frames)
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
				if(action == GLFW_PRESS){
					when(key){
						GLFW_KEY_ESCAPE -> {
							window?.close()
						}
						GLFW_KEY_ENTER -> {
							if(mods.or(GLFW_MOD_ALT) >= 0){
								window?.fullscreen = !(window?.fullscreen ?: return)
							}
						}
						GLFW_KEY_SPACE -> {
							val rot = Quaternionf()
							rot.rotateAxis(-World.playerRotation, Vector3f(0f, 1f, 0f))
							val p = map?.rayCast(World.playerPosition, rot) ?: return
							if (p.second != Float.MAX_VALUE && p.second > 0f){
								val p = Pair(p.first, explosion?.makeInstance() ?: return)
								System.out.println("Adding")
								synchronized(explosions){
									explosions.add(p)
								}
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
    }

}