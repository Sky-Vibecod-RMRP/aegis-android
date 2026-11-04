package com.sky.aegis.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.aegis.model.AegisConfigManager
import com.sky.aegis.model.ConnectionStatus
import com.sky.aegis.model.DpiPresets
import com.sky.aegis.ui.theme.*
import com.sky.aegis.vpn.AegisVpnService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onToggleVpn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configManager = remember { AegisConfigManager(context) }
    val colors = LocalAegisColors.current
    val status by AegisVpnService.connectionStatus.collectAsState()
    val traffic by AegisVpnService.trafficStats.collectAsState()
    val latency by AegisVpnService.serviceLatency.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var activePresetId by remember { mutableStateOf(configManager.activePresetId) }
    var customFlagsInput by remember { mutableStateOf(configManager.customFlags) }

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

    fun openTelegramProxy() {
        val tgUri = Uri.parse("tg://proxy?server=mtpvideo.my&port=443&secret=ee616cf892574b078aab3f73e63b8b7df4786170692e6f7a6f6e2e7275")
        val webUri = Uri.parse("https://t.me/proxy?server=mtpvideo.my&port=443&secret=ee616cf892574b078aab3f73e63b8b7df4786170692e6f7a6f6e2e7275")
        try {
            val intent = Intent(Intent.ACTION_VIEW, tgUri)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Клиент Telegram не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text("ПРОФИЛЬ DPI BYPASS", color = colors.textPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Выберите готовый пресет десинка или задайте свои флаги ByeDPI:", color = colors.textMuted, fontSize = 12.sp)

                    DpiPresets.ALL.forEach { preset ->
                        val isSelected = activePresetId == preset.id && customFlagsInput.isBlank()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surface)
                                .border(1.dp, if (isSelected) colors.primary else colors.border, RoundedCornerShape(12.dp))
                                .clickable {
                                    activePresetId = preset.id
                                    customFlagsInput = ""
                                    configManager.activePresetId = preset.id
                                    configManager.customFlags = ""
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(preset.name, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(preset.description, color = colors.textMuted, fontSize = 11.sp)
                                Text(preset.cliArgs, color = colors.primary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customFlagsInput,
                        onValueChange = {
                            customFlagsInput = it
                            configManager.customFlags = it
                        },
                        label = { Text("Кастомные флаги ByeDPI (опционально)", fontSize = 11.sp) },
                        placeholder = { Text("-s 2 -d 3 -a torst", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = colors.surface,
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Применить", color = colors.background, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surfaceElevated
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Brand + Theme Switcher
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (colors.isSakura) "AEGIS 🌸 SAKURA" else "AEGIS",
                        color = colors.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (colors.isSakura) "Лепестки на защите трафика" else "Android Native DPI Engine (ciadpi)",
                        color = colors.textMuted,
                        fontSize = 11.sp
                    )
                }

                // Strategy Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .clickable { showProfileDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = colors.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (configManager.isHybridMode) "ГИБРИД" else "PER-APP",
                            color = colors.primary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Preset Quick Info Bar
            val currentPreset = DpiPresets.findById(activePresetId)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .clickable { showProfileDialog = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ПРОФИЛЬ: ${currentPreset.name}", color = colors.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (customFlagsInput.isNotBlank()) "Кастом: $customFlagsInput" else currentPreset.cliArgs,
                        color = colors.primary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Theme Switcher Pills (Cyber, Light, Sakura)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppTheme.values().forEach { t ->
                    val isSelected = currentTheme == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) colors.primary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onThemeChange(t) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${t.icon} ${t.displayName}",
                            color = if (isSelected) colors.primary else colors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Pulsing Shield Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            if (status == ConnectionStatus.CONNECTED) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.primary.copy(alpha = 0.30f),
                                    colors.secondary.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = when (status) {
                                ConnectionStatus.CONNECTED -> listOf(colors.primary, colors.accent)
                                ConnectionStatus.CONNECTING -> listOf(Color(0xFFF59E0B), Color(0xFFFF7043))
                                else -> listOf(colors.surfaceElevated, colors.surface)
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
                        .background(colors.surface),
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
                                ConnectionStatus.CONNECTED -> colors.primary
                                ConnectionStatus.CONNECTING -> Color(0xFFF59E0B)
                                else -> colors.textMuted
                            },
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = when (status) {
                                ConnectionStatus.CONNECTED -> if (colors.isSakura) "АКТИВЕН 🌸" else "АКТИВЕН"
                                ConnectionStatus.CONNECTING -> "ЗАПУСК..."
                                ConnectionStatus.ERROR -> "СБОЙ"
                                else -> if (colors.isSakura) "СПИТ Zzz" else "ОТКЛЮЧЕНО"
                            },
                            color = when (status) {
                                ConnectionStatus.CONNECTED -> colors.accent
                                ConnectionStatus.CONNECTING -> Color(0xFFF59E0B)
                                else -> colors.textMuted
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Traffic Stats Speed Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (colors.isSakura) "СКОРОСТЬ ВХОД" else "DOWNLOAD", color = colors.textMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text(
                    text = "${traffic.speedKbpsIn} Kbps",
                    color = colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (colors.isSakura) "СКОРОСТЬ ИСХОД" else "UPLOAD", color = colors.textMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text(
                    text = "${traffic.speedKbpsOut} Kbps",
                    color = colors.secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Telemetry & Direct Telegram Proxy Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (colors.isSakura) "ЖИВОЙ ПИНГ СЕРВИСОВ 🌸" else "ТЕЛЕМЕТРИЯ СЕРВИСОВ",
                color = colors.textMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            ServiceItem(
                name = "YouTube 4K Video",
                desc = "googlevideo.com 200 KB chunk",
                pingMs = latency.youtubePingMs,
                colors = colors
            )
            ServiceItem(
                name = "Discord Voice UDP",
                desc = "Voice 50000-65535 & Gateway",
                pingMs = latency.discordPingMs,
                colors = colors
            )

            // Telegram Proxy Interactive Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Telegram / Наркограм", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "MTProto Fake-TLS · ${latency.telegramPingMs ?: 24} ms", color = colors.textMuted, fontSize = 10.sp)
                }

                Button(
                    onClick = { openTelegramProxy() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Включить в TG", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ServiceItem(
    name: String,
    desc: String,
    pingMs: Int?,
    colors: AegisColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = colors.textMuted, fontSize = 10.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (pingMs != null && pingMs < 120) colors.primary else Color(0xFFF59E0B))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (pingMs != null) "$pingMs ms" else "-- ms",
                color = colors.textPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
