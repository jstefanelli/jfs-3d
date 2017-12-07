package com.jstefanelli.jfs3d.engine

import org.lwjgl.BufferUtils
import java.io.InputStream
import java.nio.*
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Utils {
	companion object {
		@JvmStatic
		fun resizeBuffer(b: ByteBuffer, capacity: Int) :ByteBuffer{
			val bb = BufferUtils.createByteBuffer(capacity)
			bb.flip()
			bb.put(b)
			return bb
		}

		@JvmStatic
		fun ioResourceToByteBuffer(filePath: String, bufferSize: Int) : ByteBuffer?{
			var bb: ByteBuffer? = null

			val ph: Path = Paths.get(filePath)
			if(Files.isReadable(ph)){
				Files.newByteChannel(ph).use {
					val b = BufferUtils.createByteBuffer(it.size().toInt() + 1)
					while(it.read(b) != -1) ;
					bb = b
				}
			}else{
				Utils.javaClass.classLoader.getResourceAsStream(filePath).use {
					Channels.newChannel(it).use {
						var b = BufferUtils.createByteBuffer(bufferSize)

						while(true){
							val bytes = it.read(b)
							if(bytes == -1)
								break
							if(b.remaining() == 0){
								b = resizeBuffer(b, b.capacity() * 3 / 2)
							}
						}

						bb = b
					}
				}


			}

			bb?.flip()
			return bb?.slice()
		}

	}
}