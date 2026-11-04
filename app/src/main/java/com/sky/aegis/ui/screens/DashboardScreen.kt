package com.sky.aegis.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.aegis.model.ConnectionStatus
import com.sky.aegis.model.ProtectionMode
import com.sky.aegis.ui.theme.*
import com.sky.aegis.vpn.AegisVpnService

@Composable
fun DashboardScreen(
    onToggleVpn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status by AegisVpnService.connectionStatus.collectAsState()
    val traffic by AegisVpnService.trafficStats.collectAsState()
    val latency by AegisVpnService.serviceLatency.collectAsState()
    var selectedMode by remember { mutableStateOf(ProtectionMode.HYBRID_SMART) }

    // Pulse animation for active shield
    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == ConnectionStatus.CONNECTED) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AEGIS",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Android DPI Bypass & Route",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Mode Selector Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Obsidian800)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (selectedMode) {
                        ProtectionMode.HYBRID_SMART -> "HYBRID AUTO"
                        ProtectionMode.DPI_BYPASS_LOCAL -> "DPI BYPASS"
                        ProtectionMode.VPS_VLESS_TUNNEL -> "VPS VLESS"
                    },
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Animated Shield Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            // Outer Glow Ring
            if (status == ConnectionStatus.CONNECTED) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberCyan.copy(alpha = 0.25f),
                                    CyberMagenta.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Central Touch Button
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = when (status) {
                                ConnectionStatus.CONNECTED -> listOf(CyberCyan, CyberEmerald)
                                ConnectionStatus.CONNECTING -> listOf(CyberAmber, Color(0xFFFF7043))
                                else -> listOf(Obsidian800, Obsidian700)
                            }
                        )
                    )
                    .clickable { onToggleVpn() }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Obsidian900),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (status) {
                                ConnectionStatus.CONNECTED -> Icons.Default.Shield
                                ConnectionStatus.CONNECTING -> Icons.Default.Refresh
                                else -> Icons.Default.PowerSettingsNew
                            },
                            contentDescription = "Shield",
                            tint = when (status) {
                                ConnectionStatus.CONNECTED -> CyberCyan
                                ConnectionStatus.CONNECTING -> CyberAmber
                                else -> TextMuted
                            },
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (status) {
                                ConnectionStatus.CONNECTED -> "АКТИВЕН"
                                ConnectionStatus.CONNECTING -> "ЗАПУСК..."
                                ConnectionStatus.ERROR -> "СБОЙ"
                                else -> "ОТКЛЮЧЕНО"
                            },
                            color = when (status) {
                                ConnectionStatus.CONNECTED -> CyberEmerald
                                ConnectionStatus.CONNECTING -> CyberAmber
                                else -> TextMuted
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Traffic Stats Row (Speed UP / DOWN)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Obsidian900)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("DOWNLOAD", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(
                    text = "${traffic.speedKbpsIn} Kbps",
                    color = CyberCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(BorderSubtle))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("UPLOAD", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(
                    text = "${traffic.speedKbpsOut} Kbps",
                    color = CyberMagenta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Service Latency Telemetry Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ТЕЛЕМЕТРИЯ СЕРВИСОВ",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            ServiceItem(
                name = "YouTube 4K Video",
                desc = "googlevideo.com 200 KB chunk",
                pingMs = latency.youtubePingMs,
                activeColor = CyberCoral
            )
            ServiceItem(
                name = "Discord Voice UDP",
                desc = "Voice 50000-65535 & Gateway",
                pingMs = latency.discordPingMs,
                activeColor = CyberCyan
            )
            ServiceItem(
                name = "Telegram Bridge",
                desc = "DC1-DC5 Web/MTProto",
                pingMs = latency.telegramPingMs,
                activeColor = Color(0xFF29B6F6)
            )
        }
    }
}

@Composable
private fun ServiceItem(
    name: String,
    desc: String,
    pingMs: Int?,
    activeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Obsidian900)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, color = TextMuted, fontSize = 10.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (pingMs != null) CyberEmerald else Color.Gray)
            )
            Text(
                text = if (pingMs != null) "$pingMs ms" else "---",
                color = if (pingMs != null) CyberEmerald else TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
