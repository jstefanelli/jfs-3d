package com.jstefanelli.jfs3d.engine

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*

@XmlRootElement
class Config {

	@XmlElement
	var resolutionX: Int = 800
	@XmlElement
	var resolutionY: Int = 600
	@XmlElement
	var fsResolutionX: Int = 1280
	@XmlElement
	var fsResolutionY: Int = 720
	@XmlElement
	var fullscreen: Boolean = false
	@XmlElement
	var useGl3: Boolean = false

	companion object {

		@JvmStatic
		fun LoadFromFile(path: String): Config?{
			val file: File = File(path)

			val ctx = JAXBContext.newInstance(Config.javaClass)
			if(!file.exists()) {
				val cf = Config()
				val m = ctx.createMarshaller()
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
				m.marshal(cf, FileWriter(file))

				return cf
			}

			val marsh = ctx.createUnmarshaller()

			return marsh.unmarshal(file) as Config
		}

		@JvmStatic
		val default: Config? = LoadFromFile("defaultConfig.xml")

	}
}