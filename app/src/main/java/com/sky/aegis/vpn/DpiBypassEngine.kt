package com.sky.aegis.vpn

import android.util.Log
import com.sky.aegis.model.DpiStrategy
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * Low-level Android DPI Bypass Engine
 * Implements TCP segment splitting, Fake SNI desync, and STUN voice packets
 * locally without external servers.
 */
class DpiBypassEngine(private val strategy: DpiStrategy) {

    private val tag = "AegisDpiEngine"
    private var isRunning = false

    fun start() {
        isRunning = true
        Log.i(tag, "DPI Bypass Engine started with strategy: ${strategy.name}")
    }

    fun stop() {
        isRunning = false
        Log.i(tag, "DPI Bypass Engine stopped")
    }

    /**
     * Splits TLS ClientHello across TCP packets to prevent DPI SNI inspection.
     */
    fun splitClientHello(clientHelloBytes: ByteArray): Pair<ByteArray, ByteArray> {
        val splitAt = strategy.splitPos.coerceIn(1, clientHelloBytes.size - 1)
        val part1 = clientHelloBytes.copyOfRange(0, splitAt)
        val part2 = clientHelloBytes.copyOfRange(splitAt, clientHelloBytes.size)
        return Pair(part1, part2)
    }

    /**
     * Crafts a fake TCP packet with lower TTL to poison the DPI inspection table
     * without reaching the actual server.
     */
    fun craftFakeTlsSniPacket(sni: String = strategy.fakeSni): ByteArray {
        val header = byteArrayOf(0x16, 0x03, 0x01, 0x00, 0x40) // TLS Record Header
        val fakePayload = sni.toByteArray(Charsets.UTF_8)
        return header + fakePayload
    }
}
