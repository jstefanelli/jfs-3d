package com.jstefanelli.jfs3d.engine

import org.lwjgl.glfw.*

import org.lwjgl.opengl.GL11.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil

interface DrawCallback{
	fun draw()
}

class EngineWindow(title: String?){
	var title: String = "JEngine JV"
		private set

	var window: Long = 0
			get
			private set

	companion object glfwState{

		private var started: Boolean = false

		private fun initGLFW(){
			if(started)
				return

			GLFWErrorCallback.createPrint(System.err).set()

			if(!glfwInit())
				throw IllegalStateException("GLFW Init failed")

			started = true
		}
	}

	private var keyCbs: ArrayList<GLFWKeyCallbackI> = ArrayList()

	var drawCb: DrawCallback? = null

	fun addKeyCb(cb: GLFWKeyCallbackI){
		synchronized(keyCbs, {
			keyCbs.add(cb)
		})

	}

	fun removeKeyCb(cb: GLFWKeyCallbackI){
		synchronized(keyCbs, {
			if(keyCbs.contains(cb)){
				keyCbs.remove(cb)
			}
		})
	}

	var width : Int = 800
		get(){
			if(window == 0L)
				return field
			val width = intArrayOf(-1)
			glfwGetWindowSize(window, width, null)
			field = width[0]
			return field
		}
		set(value){
			field = value
			fullScreenResolutionX = value
			if(window == 0L)
				return
			val height = intArrayOf(-1)
			glfwGetWindowSize(window, null, height)
			glfwSetWindowSize(window, value, height[0])
		}

	var height: Int = 600
		get(){
			if(window == 0L)
				return field
			val height = intArrayOf(-1)
			glfwGetWindowSize(window, null, height)
			field = height[0]
			return field
		}
		set(value){
			field = value
			fullScreenResolutionY = value
			if(window == 0L)
				return
			val width = intArrayOf(-1)
			glfwGetWindowSize(window, width, null)
			glfwSetWindowSize(window, width[0], value)
		}

	var fullScreenResolutionX: Int = 800
	var fullScreenResolutionY: Int = 600

	var fullscreen: Boolean = false
		get(){
			if(window == 0L)
				return false
			field = glfwGetWindowMonitor(window) != 0L
			return field
		}
		set(value){
			field = value
			if(window == 0L)
				return
			if(value) {
				val l = glfwGetPrimaryMonitor()
				glfwSetWindowMonitor(window, l, 0, 0, fullScreenResolutionX, fullScreenResolutionY, GLFW_DONT_CARE)
				if(doVsync)
					glfwSwapInterval(1)
			}else{
				glfwSetWindowMonitor(window, 0L, 100, 100, width, height, GLFW_DONT_CARE)
				if(doVsync)
					glfwSwapInterval(1)
			}
		}

	private var requestedExplicitGlContext = false

	var requestedGlContextMajor: Int = 0
		private set

	var requestedGlContextMinor: Int = 0
		private set

	var requestedCompatibilityContext: Boolean = false
		private set


	var doVsync: Boolean = false
		get
		set(value){
			field = value
			if(window == 0L)
				return
			if(value)
				glfwSwapInterval(1)
			else
				glfwSwapInterval(1)
		}

	fun make(){
		initGLFW()
        glfwDefaultWindowHints();
        if(requestedExplicitGlContext){
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, requestedGlContextMajor)
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, requestedGlContextMinor)

			if(requestedGlContextMajor > 3 || (requestedGlContextMajor == 3 && requestedGlContextMinor >= 2) && requestedCompatibilityContext)
				glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE)
		}

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

		window = glfwCreateWindow(if(width == -1) 800 else width, if(height == -1) 600 else height, title, MemoryUtil.NULL, MemoryUtil.NULL)
		if(window == MemoryUtil.NULL)
			throw RuntimeException("Failed to create window")


		glfwSetKeyCallback(window, { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if(window != 0L) {
                var arr: Array<GLFWKeyCallbackI>? = null
                synchronized(keyCbs, {
                    arr = keyCbs.toTypedArray()
                })
                for (cb in arr ?: emptyArray<GLFWKeyCallbackI>()) {
                    cb.invoke(window, key, scancode, action, mods)
                }
            }
		})
	}

    private var oldWindow: Long = 0L

	fun run(){
		glfwMakeContextCurrent(window)

		GL.createCapabilities()

		if(doVsync)
			glfwSwapInterval(1)

		glfwShowWindow(window)

		if(window != 0L) {
            while (!glfwWindowShouldClose(window)) {
                if (window == 0L) {
                    glfwSetKeyCallback(oldWindow, null)
                    glfwDestroyWindow(oldWindow)
                }

                glfwPollEvents()

                glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))

                drawCb?.draw()
                if (window == 0L)
                    break
                glfwSwapBuffers(window)
            }
        }
	}



    fun close(){
		if(window != 0L) {
			oldWindow = window
			window = 0L
			World.log.close()
		}
	}

	init{
		if(title != null) {
            this.title = title
        }
	}

	constructor(title: String?, glVersionMajor: Int, glVersionMinor: Int, useCompatibilityContext: Boolean) : this(title) {
		requestedExplicitGlContext = true
		requestedGlContextMajor = glVersionMajor
		requestedGlContextMinor = glVersionMinor
		requestedCompatibilityContext = useCompatibilityContext
	}
}