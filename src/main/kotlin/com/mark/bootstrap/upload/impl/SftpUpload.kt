package com.mark.bootstrap.upload.impl

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.mark.bootstrap.upload.Uploader
import me.tongfei.progressbar.ProgressBar
import java.io.File
import java.util.*


class SftpUpload(
    val props : File,
    val type : String,
    val passiveMode : Boolean
) : Uploader() {

    init {
        if (!props.exists()) {
            error("Props: ${props.path} Is Missing")
        }
    }

    lateinit var channelSftp : ChannelSftp

    override var connected: Boolean = false

    override fun baseDirectory(): String {
        return ""
    }

    override fun upload(file: File, bar : ProgressBar) {

        val fileType = when(file.name.endsWith(".jar")) {
            true -> "Jar"
            false -> "Json"
        }

        bar.extraMessage = "Uploading: ${file.name}"
        println("Uploading: ${file.name} (${type}) / (${"/client/${type}/repo/${file.name}"})")

        try {
            if (fileType == "Jar") {
                channelSftp.put(file.inputStream(),"/client/${type}/repo/${file.name}")
            } else {
                channelSftp.put(file.inputStream(),"/client/${type}/${file.name}")
            }
        }catch (ex: Exception) {
            ex.printStackTrace();
        }

    }

    override fun connect() {
        val properties = Properties()
        properties.load(props.inputStream())
        val port = properties.getProperty("port")?.toIntOrNull() ?: 21 // Default FTP port is 21
        val host = properties.getProperty("host")
        val username = properties.getProperty("username")
        val password = properties.getProperty("password")
        val session = JSch().getSession(username, host, port)

        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)
        session.connect()

        // 2. Create the SFTP channel and connect
        val channel = session.openChannel("sftp")
        channel.connect()

        channelSftp = channel as ChannelSftp

        if (channelSftp.isConnected) {
            println("FTP Logged in")
            channelSftp.mkdir("/client")
            channelSftp.mkdir("/client/${type}")
            channelSftp.mkdir("/client/${type}/repo/")
            connected = true
        } else {
            error("Error Logging into FTP")
        }
    }


    override fun connected() = channelSftp.isConnected

}