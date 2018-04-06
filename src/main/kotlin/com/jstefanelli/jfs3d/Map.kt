package com.jstefanelli.jfs3d

import com.jstefanelli.jfs3d.engine.*
import com.jstefanelli.jfs3d.engine.geometry.rect
import com.jstefanelli.jfs3d.engine.utils.CommandParser
import com.jstefanelli.jfs3d.engine.utils.Console
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallbackI
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import org.lwjgl.opengl.GL11.*
import javax.sound.midi.Track
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Map(val mapFile: InputStream, val interactive: Boolean = false, val cfg: Config) : CommandParser {

    override fun parseCommand(command: String) {
        if(entityRegex.matcher(command).matches()){
            val res = entityRegex.matcher(command)
            res.find()
            val index = res.group(1).toInt()
            if(!entityTypes.containsKey(index)){
                World.log.err(TAG, "Entity type not found. Slipping...")
	            return
            }
            val x = res.group(2).toFloat()
            val y = res.group(3).toFloat()
            val z = res.group(4).toFloat()
            val ent = TrackedEntity(entityTypes.get(index) ?: return, Vector3f(x, y, z))
            ent.load()
            synchronized(entityList) {
                entityList.add(ent)
            }
            World.log.log(TAG, "Adding entity at position: $x/$y/$z")
            return
        }
	    if(entityRotationRegex.matcher(command).matches()){
		    val res = entityRotationRegex.matcher(command)
		    res.find()
		    val index = res.group(1).toInt()
		    if(!entityTypes.containsKey(index)){
			    System.err.println("Entity type key not found. Skipping.,,")
			    return
		    }
		    val x = res.group(2).toFloat()
		    val y = res.group(3).toFloat()
		    val z = res.group(4).toFloat()
		    val o = res.group(5).toFloat()
		    val e = TrackedEntity(entityTypes.get(index) ?: return, Vector3f(x, y, z))
		    val or = Quaternionf()
		    or.fromAxisAngleDeg(Vector3f(0f, 1f, 0f), o)
		    e.orientation = or
		    synchronized(entityList) {
			    entityList.add(e)
		    }
		    return
	    }

        if(cubeRegex.matcher(command).matches()){
            val res = cubeRegex.matcher(command)
            res.find()
            val x = res.group(1).toFloat()
            val y = res.group(2).toFloat()
            val z = res.group(3).toFloat()
            val pos = Vector3f(x, y, z)
            synchronized(entityList) {
                if (!cubeMode) {
                    entityList.add(MappedCube(pos, lastColor))
                } else {
                    entityList.add(MappedCube(pos, lastTexture ?: return))
                }
            }
            return
        }
	    if(pickupRegex.matcher(command).matches()){
		    val res = pickupRegex.matcher(command)
		    res.find()
		    val index = res.group(1).toInt()
		    if(!entityTypes.containsKey(index)){
			    World.log.err(TAG, "Pickup type key ($index) not found. Skipping...")
			    return
		    }
		    val x = res.group(2).toFloat()
		    val y = res.group(3).toFloat()
		    val z = res.group(4).toFloat()
		    val pt = entityTypes.get(index) ?: return
		    synchronized(entityList) {
			    entityList.add(MappablePickup(pt, (Vector3f(x, y, z)), Vector2f(1.0f, 1.0f), true))
		    }
		    return
	    }


	    if(pickupSizeRegex.matcher(command).matches()){
		    val res = pickupSizeRegex.matcher(command)
		    res.find()
		    val index = res.group(1).toInt()
		    if(!entityTypes.containsKey(index)){
			    World.log.err(TAG, "Pickup type key ($index) not found. Skipping...")
			    return
		    }
		    val x = res.group(2).toFloat()
		    val y = res.group(3).toFloat()
		    val z = res.group(4).toFloat()
		    val u = res.group(5).toFloat()
		    val v = res.group(6).toFloat()
		    val pt = entityTypes.get(index) ?: return
		    synchronized(entityList) {
			    entityList.add(MappablePickup(pt, (Vector3f(x, y, z)), Vector2f(u, v), true))
		    }
		    return
	    }

	    if(resRegex.matcher(command).matches()){
		    val w = World.currentWindow ?: return
		    val res = resRegex.matcher(command)
		    res.find()
		    val x = res.group(1).toInt()
		    val y = res.group(2).toInt()
		    if(w.fullscreen){
			    w.fullScreenResolutionX = x
			    w.fullScreenResolutionY = y
			    cfg.fsResolutionX = x
			    cfg.fsResolutionY = y
		    }else{
			    w.width = x
			    w.height = y
			    cfg.resolutionX = x
			    cfg.resolutionY = y
		    }

		    cfg.saveToFile(Config.defaultPath)
		    return
	    }
	    if(fullScreenRegex.matcher(command).matches()){
		    val w = World.currentWindow ?: return
		    w.fullscreen = !w.fullscreen
		    cfg.fullscreen = w.fullscreen
		    cfg.saveToFile(Config.defaultPath)
	    }
    }

    private val entityList: ArrayList<MappableEntity> = ArrayList()

    private var floorColor = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    private var ceilColor = Vector4f(0.3f, 0.3f, 0.3f, 1f)

	private var floorTexture: Texture? = null
	private var ceilTexture: Texture? = null
    private var consoleInst: Console? = null

	private val entityTypes: HashMap<Int, Entity> = HashMap()
	val player: Player = Player()

	var safetyDistance: Float = 0.03f
	var myFont: BaseFont? = null

	private var movementSpeed = 1.8f
	private var rotateSensitivity = 3.5f

	private var lastShot = 0L
	private var shotRate = 250L


    private var lastColor = Vector4f(0f, 0f, 1f, 1f)
    private var lastTexture: Texture? = null
    private var cubeMode = false

    companion object {
	    @JvmStatic
	    private val TAG = "MAP"
        private val cubeColor = Pattern.compile("^cc\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
        private val cubeRegex = Pattern.compile("^c\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
        private val floorRegex = Pattern.compile("^fl\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
        private val ceilingRegex = Pattern.compile("^ce\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
        private val cubeTexture = Pattern.compile("^ct\\s+(.+)\\s*$")
	    private val floorTextureRegex = Pattern.compile("^ft\\s+(.+)\\s*$")
        private val overRegex = Pattern.compile("^over\\s*$")
		private val entityTypeRegex = Pattern.compile("^et\\s+(\\d+)\\s+([^\\s]+)\\s*$")
		private val entityRegex = Pattern.compile("^e\\s+(\\d+)\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
	    private val entityRotationRegex = Pattern.compile("^e\\s+(\\d+)\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+r\\s+$floatValuePattern\\s*$")
	    private val pickupRegex = Pattern.compile("^p\\s+(\\d+)\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
	    private val pickupSizeRegex = Pattern.compile("^p\\s+(\\d+)\\s+$floatValuePattern\\s+$floatValuePattern\\s+$floatValuePattern\\s+s\\s+$floatValuePattern\\s+$floatValuePattern\\s*$")
	    private val fontRegex = Pattern.compile("^font\\s+(.+)\\s*$")
	    private val resRegex = Pattern.compile("^set-resolution\\s+(\\d+)\\s+(\\d+)\\s*$")
	    private val fullScreenRegex = Pattern.compile("^switch-fs\\s*$")
    }


    var explosion: JSpriteType? = null
    var explosions: ArrayList<Pair<Vector3f, JSpriteType.JSpriteInstance>> = ArrayList()
		get
		private set

    fun parse(){
        val reader = Scanner(mapFile)
	    var selectedFont = "C:\\Windows\\Fonts\\Arial.ttf"

        while(true){
            if(!interactive && !reader.hasNextLine()) {
                break
            }
            val line: String = reader.nextLine()
            if(overRegex.matcher(line).matches()){
                break
            }
            if(cubeColor.matcher(line).matches()){
                val res = cubeColor.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                lastColor = Vector4f(r, g, b, a)
                cubeMode = false
                continue
            }
            if(cubeTexture.matcher(line).matches()){
                val res = cubeTexture.matcher(line)
                res.find()
                val str = res.group(1)
                cubeMode = true
                lastTexture = Texture.make(str, cfg)
                continue
            }
	        if(floorTextureRegex.matcher(line).matches()){
		        val res = floorTextureRegex.matcher(line)
		        res.find()
		        val str = res.group(1)
		        floorTexture = Texture.make(str, cfg)
				floorTexture?.doAnisotropy = true
		        continue
	        }
            if(cubeRegex.matcher(line).matches()) {
                val res = cubeRegex.matcher(line)
                res.find()
                val x = res.group(1).toFloat()
                val y = res.group(2).toFloat()
                val z = res.group(3).toFloat()
                val pos = Vector3f(x, y, z)
                if(!cubeMode) {
                    entityList.add(MappedCube(pos, lastColor))
                }else{
                    entityList.add(MappedCube(pos, lastTexture ?: return))
                }
                continue
            }
            if(floorRegex.matcher(line).matches()){
                val res = floorRegex.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                floorColor = Vector4f(r, g, b, a)
                continue
            }
            if(ceilingRegex.matcher(line).matches()){

                val res = ceilingRegex.matcher(line)
                res.find()
                val r = res.group(1).toFloat()
                val g = res.group(2).toFloat()
                val b = res.group(3).toFloat()
                val a = res.group(4).toFloat()
                ceilColor = Vector4f(r, g, b, a)
                continue
            }
			if(entityTypeRegex.matcher(line).matches()){
				val res = entityTypeRegex.matcher(line)
				res.find()
				val index = res.group(1).toInt()
				if(entityTypes.containsKey(index)){
					System.err.println("Found duplicate entity type keys. Skipping...")
					continue
                }
				val path = res.group(2)
				val et = Entity(path, cfg)
				entityTypes.put(index, et)
				continue
            }
	        if(entityRotationRegex.matcher(line).matches()){
		        val res = entityRotationRegex.matcher(line)
		        res.find()
		        val index = res.group(1).toInt()
		        if(!entityTypes.containsKey(index)){
			        System.err.println("Entity type key not found. Skipping.,,")
			        continue
		        }
		        val x = res.group(2).toFloat()
		        val y = res.group(3).toFloat()
		        val z = res.group(4).toFloat()
		        val o = res.group(5).toFloat()
		        val e = TrackedEntity(entityTypes.get(index) ?: return, Vector3f(x, y, z))
				val or = Quaternionf()
		        or.fromAxisAngleDeg(Vector3f(0f, 1f, 0f), o)
		        e.orientation = or
		        entityList.add(e)
		        continue
	        }
			if(entityRegex.matcher(line).matches()){
				val res = entityRegex.matcher(line)
				res.find()
				val index = res.group(1).toInt()
				if(!entityTypes.containsKey(index)){
					System.err.println("Entity type key not found. Skipping.,,")
					continue
                }
				val x = res.group(2).toFloat()
				val y = res.group(3).toFloat()
				val z = res.group(4).toFloat()
				entityList.add(TrackedEntity(entityTypes.get(index) ?: return, Vector3f(x, y, z)))
				continue
            }
	        if(pickupRegex.matcher(line).matches()){
		        val res = pickupRegex.matcher(line)
		        res.find()
		        val index = res.group(1).toInt()
		        if(!entityTypes.containsKey(index)){
			        World.log.err(TAG, "Pickup type key ($index) not found. Skipping...")
			        continue
		        }
		        val x = res.group(2).toFloat()
		        val y = res.group(3).toFloat()
		        val z = res.group(4).toFloat()
		        val pt = entityTypes.get(index) ?: continue
		        entityList.add(MappablePickup(pt, (Vector3f(x, y, z)), Vector2f(1.0f, 1.0f), true))
		        continue
	        }
	        if(pickupSizeRegex.matcher(line).matches()){
		        val res = pickupSizeRegex.matcher(line)
		        res.find()
		        val index = res.group(1).toInt()
		        if(!entityTypes.containsKey(index)){
			        World.log.err(TAG, "Pickup type key ($index) not found. Skipping...")
			        continue
		        }
		        val x = res.group(2).toFloat()
		        val y = res.group(3).toFloat()
		        val z = res.group(4).toFloat()
		        val u = res.group(5).toFloat()
		        val v = res.group(6).toFloat()
		        val pt = entityTypes.get(index) ?: continue
		        entityList.add(MappablePickup(pt, (Vector3f(x, y, z)), Vector2f(u, v), true))
		        continue
	        }
	        if(fontRegex.matcher(line).matches()){
				val res = fontRegex.matcher(line)
		        res.find()
		        selectedFont = res.group(1)
		        continue
	        }
	        World.log.warn(TAG, "Ignoring line: ")
	        World.log.warn(TAG, line)
        }
        for(p in entityList){
            p.load()
        }


		entityList.add(player)
	    val l = floorTexture?.load()
		if(l == false) System.err.println("Failed to load floor texture")

		try{
			myFont = BaseFont(selectedFont)
		}catch(e: Exception){
			myFont = BaseFont("C:\\Window\\Fonts\\Arial.ttf")
		}


	    myFont?.fontHeight = 100f
	    myFont?.load()

        consoleInst = Console(this, myFont ?: return, Vector4f(0.2f, 0.2f, 0.2f, 0.4f), 200f)
        World.log.consoleOut = consoleInst
        World.currentWindow?.addKeyCb(GLFWKeyCallbackI { window, key, scancode, action, mods -> consoleInst?.parseKey(key, scancode, action, mods) })

        explosion = JSpriteType("textures/frames/explosion.jfr", cfg)
        explosion?.load()
		
    }

	fun movePlayer(movement: Vector3f){
		movement.rotateAxis(World.playerRotation, 0f, -1f, 0f)
		val tmp = Vector3f(World.playerPosition)
		tmp.add(movement)
		if(validateMovement(tmp, true))
			World.playerPosition = tmp
	}

	fun movePlayer(x: Float, y: Float, z: Float){
		movePlayer(Vector3f(x, y, z))
	}

	fun updateMap(){
		val win = World.currentWindow ?: return
		if((win.window) == 0L) return

		if(consoleInst?.shown != true) {
			if (GLFW.glfwGetKey(win.window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
				World.playerRotation -= 0.01f * rotateSensitivity
			}
			if (GLFW.glfwGetKey(win.window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
				World.playerRotation += 0.01f * rotateSensitivity
			}
			if (GLFW.glfwGetKey(win.window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
				movePlayer(0f, 0f, -0.02f * movementSpeed)
			}
			if (GLFW.glfwGetKey(win.window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
				movePlayer(0f, 0f, +0.02f * movementSpeed)
			}

			val time = System.currentTimeMillis()

			val space_status = GLFW.glfwGetKey(win.window, GLFW.GLFW_KEY_SPACE)
			if (space_status == GLFW.GLFW_PRESS && (time - lastShot > shotRate) && player.ammo > 0) {
				val rot = Quaternionf()
				player.ammo--
				rot.rotateAxis(-World.playerRotation, Vector3f(0f, 1f, 0f))
				val p = rayCast(World.playerPosition, rot, true)
				if (p.second != Float.MAX_VALUE && p.second > 0f) {
					val pa = Pair(p.first, explosion?.makeInstance() ?: return)
					synchronized(explosions) {
						val ex = explosions
						ex.add(pa)
					}
				}
				lastShot = time
			}
		}
		player.position = World.playerPosition
		player.orientation.fromAxisAngleRad(0f, 1f, 0f, World.playerRotation)
		synchronized(entityList){
			for (p in entityList){
				p.update(this)
            }
		}
	}

    fun drawMap(){
        val floor = World.floor ?: return
	    if(floorTexture == null)
            floor.drawAt(Vector3f(0f, -0.5f, 0f), floorColor)
	    else {
		    val tx = floorTexture ?: return
		    floor.drawAt(Vector3f(0f, -0.5f, 0f), tx.textureId)
	    }
	    if(ceilTexture == null)
            floor.drawAt(Vector3f(0f, 0.5f, 0f), ceilColor)
	    else{
		    val tx = ceilTexture ?: return
		    floor.drawAt(Vector3f(0f, 0.5f, 0f), tx.textureId)
	    }
	    synchronized(entityList) {
		    for (p in entityList) {
				p.draw()
		    }
	    }

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

        glClear(GL_DEPTH_BUFFER_BIT)

        myFont?.drawTextAt("Health: " + player.lp, Vector2f(10f, 10f), Vector4f(0f, 0f, 0.2f, 1f), 0.25f)
	    myFont?.drawTextAt("Ammo: " + player.ammo, Vector2f(10f, 30f), Vector4f(0f, 0f, 0.2f, 1f), 0.25f)
        consoleInst?.draw()

    }

    fun validateMovement(position: Vector3f, isPlayer: Boolean = false, me: MappableEntity? = null): Boolean{
	    synchronized(entityList) {
		    for (c in entityList) {
				if(c == me)
					continue
				if(c == player && isPlayer)
					continue
			    if (c.active && position.x >= c.position.x - c.size.x / 2 - safetyDistance && position.x <= c.position.x + c.size.x / 2 + safetyDistance
					    && position.z >= c.position.z - c.size.y / 2 - safetyDistance && position.z <= c.position.z + c.size.y / 2 + safetyDistance) {
				    c.onCollide(player)
				    if (c.collides)
					    return false
			    }
		    }
	    }
        return true
    }



	fun myDistance(pos: Vector3f, rot: Quaternionf, pos2: Vector3f, isPlayer: Boolean = false) : Float{
		val dist = pos2.distance(pos)
		val forward = Vector3f(0f, 0f, -dist)
		val pos2x = Vector3f(pos2)
		pos2x.sub(pos)
		forward.rotate(rot)

		if(pos2x.x * forward.x >= 0f && pos2x.z * forward.z >= 0f)
			return dist
		else
			return -dist
	}

	fun rayCast(position: Vector3f, orientation: Quaternionf, isPlayer: Boolean = false) : Pair<Vector3f, Float>{

		val p = rayCastToEntity(position, orientation, isPlayer)

		if(p.first == null)
			return Pair(Vector3f(), Float.MAX_VALUE);

		val dist = myDistance(position, orientation, p.second)
		p.first?.onHit()
		return Pair(p.second, dist)
	}

	fun rayCastToEntity(position: Vector3f, orientation: Quaternionf, isPlayer: Boolean = false, me: MappableEntity? = null): Pair<MappableEntity?, Vector3f>{
        var lastDist = Float.MAX_VALUE
        var lastPoint = Vector3f()

        val forward = Vector3f(0f, 0f, -1f)

        forward.rotate(orientation)
        forward.add(position)
        val line = rect(position, forward)
        var lastHitEntity: MappableEntity? = null
        synchronized(entityList) {
            for (c in entityList) {
				if(c == me)
					continue
				if(c == player && isPlayer)
					continue
                if(!c.active)
                    continue
	            if(!c.blocksHit)
		            continue
	            val x0 = line.getX(c.position.z - c.size.y / 2)
                val x1 = line.getX(c.position.z + c.size.y / 2)

                if (x0 >= c.position.x - c.size.x / 2 && x0 <= c.position.x + c.size.x / 2) {
                    val pt = Vector3f(x0, 0f, c.position.z - c.size.y / 2)
                    val dist = myDistance(position, orientation, pt)

                    if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
                        lastDist = dist
                        lastPoint = pt
                    }
                }
                if (x1 >= c.position.x - c.size.x / 2 && x1 <= c.position.x + c.size.x / 2) {
                    val pt = Vector3f(x1, 0f, c.position.z + c.size.y / 2)
                    val dist = myDistance(position, orientation, pt)

                    if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
                        lastDist = dist
                        lastPoint = pt
                    }
                }
                val y0 = line.getY(c.position.x - c.size.x / 2)
                val y1 = line.getY(c.position.x + c.size.x / 2)

                if (y0 >= c.position.z - c.size.y / 2 && y0 <= c.position.z + c.size.y / 2) {
                    val pt = Vector3f(c.position.x - c.size.x / 2, 0f, y0)
                    val dist = myDistance(position, orientation, pt)

                    if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
                        lastDist = dist
                        lastPoint = pt
                    }
                }
                if (y1 >= c.position.z - c.size.y / 2 && y1 <= c.position.z + c.size.y / 2) {
                    val pt = Vector3f(c.position.x + c.size.x / 2, 0f, y1)
                    val dist = myDistance(position, orientation, pt)

                    if(dist >= 0 && dist < lastDist) {
                        lastHitEntity = c
                        lastDist = dist
                        lastPoint = pt
                    }
                }
            }
        }
		return Pair(lastHitEntity, lastPoint)
	}
}