package com.jstefanelli.jfs3d.engine

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*

@XmlRootElement(name = "Config")
@XmlAccessorType(XmlAccessType.FIELD)
class Config {

	@field:XmlElement
	var resolutionX: Int = 800
	@field:XmlElement
	var resolutionY: Int = 600
	@field:XmlElement
	var fsResolutionX: Int = 1280
	@field:XmlElement
	var fsResolutionY: Int = 720
	@field:XmlElement
	var fullscreen: Boolean = false
	@field:XmlElement
	var useGl3: Boolean = false

	fun saveToFile(path: String){
		val file = File(path)
		val ctx = JAXBContext.newInstance(Config::class.java)

		val m = ctx.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		val writer = FileWriter(file)
		m.marshal(this, writer)
		writer.close()
	}

	companion object {

		@JvmStatic
		fun LoadFromFile(path: String): Config?{
			val file: File = File(path)

			val ctx = JAXBContext.newInstance(Config::class.java)
			if(!file.exists()) {
				val cf = Config()
				val m = ctx.createMarshaller()
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
				val writer = FileWriter(file)
				m.marshal(cf, writer)
				writer.close()

				return cf
			}

			val marsh = ctx.createUnmarshaller()

			return marsh.unmarshal(file) as Config?
		}

		@JvmStatic
		val defaultPath: String = "defaultConfig.xml"

		@JvmStatic
		val default: Config? = LoadFromFile(defaultPath)
	}
}