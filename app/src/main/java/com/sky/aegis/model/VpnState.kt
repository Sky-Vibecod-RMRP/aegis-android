package com.sky.aegis.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

enum class ProtectionMode {
    DPI_BYPASS_LOCAL,   // Pure local packet fragmentation without VPS
    VPS_VLESS_TUNNEL,   // Full L3 TUN VLESS Reality tunnel
    HYBRID_SMART        // Auto-failover: DPI Bypass for domestic, VPS for blocked
}

data class TrafficStats(
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val speedKbpsIn: Long = 0,
    val speedKbpsOut: Long = 0
)

data class ServiceLatency(
    val youtubePingMs: Int? = null,
    val discordPingMs: Int? = null,
    val telegramPingMs: Int? = null
)
