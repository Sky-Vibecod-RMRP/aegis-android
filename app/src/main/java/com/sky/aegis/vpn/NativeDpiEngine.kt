package com.sky.aegis.vpn

import android.content.Context
import android.os.ParcelFileDescriptor
import android.system.Os
import android.system.OsConstants
import android.util.Log
import com.sky.aegis.model.DpiStrategy
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class NativeDpiEngine(private val context: Context) {

    private val tag = "NativeDpiEngine"
    private var ciadpiProcess: Process? = null
    private var tun2socksProcess: Process? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val isRunning: Boolean
        get() = (ciadpiProcess?.isAlive == true) && (tun2socksProcess?.isAlive == true)

    fun start(
        pfd: ParcelFileDescriptor,
        strategy: DpiStrategy,
        customFlags: String = ""
    ): Boolean {
        stop()

        val nativeDir = File(context.applicationInfo.nativeLibraryDir)
        val ciadpiBin = File(nativeDir, "libciadpi.so")
        val tun2socksBin = File(nativeDir, "libtun2socks.so")

        if (!ciadpiBin.exists() || !tun2socksBin.exists()) {
            Log.e(tag, "Native binaries missing in ${nativeDir.absolutePath}")
            return false
        }

        try {
            ciadpiBin.setExecutable(true, false)
            tun2socksBin.setExecutable(true, false)
        } catch (e: Exception) {
            Log.w(tag, "Failed to set executable flags on binaries", e)
        }

        // 1. Launch ciadpi (ByeDPI SOCKS5 engine on 127.0.0.1:1080)
        try {
            val ciadpiArgs = mutableListOf(ciadpiBin.absolutePath, "-i", "127.0.0.1", "-p", "1080")
            val flagsToUse = if (customFlags.isNotBlank()) customFlags else strategy.cliArgs
            flagsToUse.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.forEach {
                ciadpiArgs.add(it)
            }

            Log.i(tag, "Starting ciadpi: ${ciadpiArgs.joinToString(" ")}")
            val ciadpiPb = ProcessBuilder(ciadpiArgs)
            ciadpiPb.redirectErrorStream(true)
            val proc = ciadpiPb.start()
            ciadpiProcess = proc

            scope.launch {
                try {
                    val reader = BufferedReader(InputStreamReader(proc.inputStream))
                    var line: String?
                    while (proc.isAlive && reader.readLine().also { line = it } != null) {
                        Log.d("Ciadpi", line ?: "")
                    }
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to start ciadpi", e)
            stop()
            return false
        }

        // Give ciadpi 150ms to bind to port 1080
        Thread.sleep(150)

        // 2. Clear FD_CLOEXEC so tun2socks inherits the TUN file descriptor
        val tunFd = pfd.fd
        try {
            Os.fcntlInt(pfd.fileDescriptor, OsConstants.F_SETFD, 0)
            Log.i(tag, "Successfully cleared FD_CLOEXEC for TUN fd: $tunFd")
        } catch (e: Exception) {
            Log.e(tag, "Failed to clear FD_CLOEXEC on TUN fd $tunFd", e)
        }

        // 3. Launch tun2socks bridging TUN fd to local SOCKS5 proxy
        try {
            val tun2socksArgs = listOf(
                tun2socksBin.absolutePath,
                "-device", "fd://$tunFd",
                "-proxy", "socks5://127.0.0.1:1080",
                "-loglevel", "warning"
            )

            Log.i(tag, "Starting tun2socks: ${tun2socksArgs.joinToString(" ")}")
            val tun2socksPb = ProcessBuilder(tun2socksArgs)
            tun2socksPb.redirectErrorStream(true)
            val proc = tun2socksPb.start()
            tun2socksProcess = proc

            scope.launch {
                try {
                    val reader = BufferedReader(InputStreamReader(proc.inputStream))
                    var line: String?
                    while (proc.isAlive && reader.readLine().also { line = it } != null) {
                        Log.d("Tun2socks", line ?: "")
                    }
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to start tun2socks", e)
            stop()
            return false
        }

        return true
    }

    fun stop() {
        try {
            tun2socksProcess?.destroy()
            ciadpiProcess?.destroy()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping native processes", e)
        }

        tun2socksProcess = null
        ciadpiProcess = null
        scope.coroutineContext.cancelChildren()
        Log.i(tag, "Native engines terminated")
    }
}
