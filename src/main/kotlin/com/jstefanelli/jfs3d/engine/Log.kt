package com.jstefanelli.jfs3d.engine

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter

class Log(val filePath: String) {
    private var stream: FileOutputStream? = null
    var doEcho = true
    init {
        val f: File = File(filePath)
        stream = FileOutputStream(f, false)
    }

    fun log(tag: String, msg: String){
        val w = stream?.writer() ?: return
        w.write("LOG $tag: $msg\r\n")
        w.flush()
        if(doEcho) System.out.println("LOG: $tag: $msg")
    }

    fun err(tag: String, msg: String){
        val w = stream?.writer() ?: return
        w.write("ERR $tag: $msg\r\n")
        w.flush()
        if(doEcho) System.out.println("ERR: $tag: $msg")
    }

    fun warn(tag: String, msg: String){
        val w = stream?.writer() ?: return
        w.write("WRN: $tag: $msg\r\n")
        w.flush()
        if(doEcho) System.out.println("WRN: $tag: $msg")
    }

    fun close(){
        stream?.close()
    }

}