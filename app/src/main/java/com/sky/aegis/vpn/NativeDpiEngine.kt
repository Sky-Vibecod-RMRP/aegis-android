package com.sky.aegis.vpn

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import com.sky.aegis.model.DpiStrategy
import io.github.dovecoteescapee.byedpi.core.ByeDpiProxy
import io.github.dovecoteescapee.byedpi.core.TProxyService
import kotlinx.coroutines.*
import java.io.File

class NativeDpiEngine(private val context: Context) {

    private val tag = "NativeDpiEngine"
    private val byeDpiProxy = ByeDpiProxy()
    private var proxyJob: Job? = null
    private var isStarted = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val isRunning: Boolean
        get() = isStarted

    fun start(
        pfd: ParcelFileDescriptor,
        strategy: DpiStrategy,
        customFlags: String = ""
    ): Boolean {
        stop()

        try {
            // 1. Prepare ByeDPI CLI arguments
            val argsList = mutableListOf("ciadpi", "-i", "127.0.0.1", "-p", "1080")
            val flagsToUse = if (customFlags.isNotBlank()) customFlags else strategy.cliArgs
            flagsToUse.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.forEach {
                argsList.add(it)
            }

            Log.i(tag, "Starting ByeDPI JNI with args: ${argsList.joinToString(" ")}")
            proxyJob = scope.launch {
                try {
                    val code = byeDpiProxy.startProxy(argsList.toTypedArray())
                    Log.i(tag, "ByeDPI JNI returned code: $code")
                } catch (e: Exception) {
                    Log.e(tag, "ByeDPI JNI start error", e)
                }
            }

            // Give proxy 100ms to bind to 127.0.0.1:1080
            Thread.sleep(100)

            // 2. Prepare hev-socks5-tunnel yaml config
            val tun2socksConfig = """
            |tunnel:
            |  name: tun0
            |  mtu: 8500
            |  ipv4: 10.10.10.10
            |  ipv6: 'fd00::1'
            |socks5:
            |  port: 1080
            |  address: 127.0.0.1
            |  udp: 'udp'
            |misc:
            |  task-stack-size: 81920
            """.trimMargin("|")

            val configFile = File(context.cacheDir, "hev_tunnel.tmp")
            configFile.writeText(tun2socksConfig)

            // 3. Start TProxyService directly with TUN fd
            Log.i(tag, "Starting TProxyService on TUN fd ${pfd.fd}")
            TProxyService.TProxyStartService(configFile.absolutePath, pfd.fd)

            isStarted = true
            return true
        } catch (e: Exception) {
            Log.e(tag, "Failed to start Native DPI Engine", e)
            stop()
            return false
        }
    }

    fun stop() {
        if (!isStarted) return
        isStarted = false

        try {
            Log.i(tag, "Stopping TProxyService...")
            TProxyService.TProxyStopService()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping TProxyService", e)
        }

        try {
            val configFile = File(context.cacheDir, "hev_tunnel.tmp")
            if (configFile.exists()) configFile.delete()
        } catch (_: Exception) {}

        try {
            runBlocking {
                withTimeoutOrNull(1000) {
                    byeDpiProxy.stopProxy()
                }
            }
            proxyJob?.cancel()
            proxyJob = null
        } catch (e: Exception) {
            Log.w(tag, "Error stopping ByeDPI proxy", e)
        }

        Log.i(tag, "Native DPI Engine fully stopped")
    }
}
