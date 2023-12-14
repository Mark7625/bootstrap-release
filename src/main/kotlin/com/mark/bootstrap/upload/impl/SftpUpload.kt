package com.mark.bootstrap.upload.impl

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
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

    lateinit var channel : Channel

    lateinit var session : Session

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
        session = JSch().getSession(username, host, port)

        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)
        session.connect()

        // 2. Create the SFTP channel and connect
        channel = session.openChannel("sftp")
        channel.connect()

        channelSftp = channel as ChannelSftp

        if (channelSftp.isConnected) {
            println("FTP Logged in")
            exists("/client")
            exists("/client/${type}")
            exists("/client/${type}/repo/")
            channelSftp.cd("/")
            connected = true
        } else {
            error("Error Logging into FTP")
        }
    }

    fun exists(remoteDir : String) {
        try {
            channelSftp.cd(remoteDir)
        } catch (e: Exception) {
            channelSftp.mkdir(remoteDir)
        }
    }


    override fun connected() = channelSftp.isConnected

    override fun close(): Boolean {
        channelSftp.exit()
        channel.disconnect()
        session.disconnect()
        return true
    }

}