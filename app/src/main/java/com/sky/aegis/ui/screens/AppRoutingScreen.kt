package com.sky.aegis.ui.screens

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.aegis.model.AegisConfigManager
import com.sky.aegis.model.AppInfo
import com.sky.aegis.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoutingScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configManager = remember { AegisConfigManager(context) }
    var isHybridMode by remember { mutableStateOf(configManager.isHybridMode) }
    var selectedPackages by remember { mutableStateOf(configManager.selectedPackages) }
    var searchQuery by remember { mutableStateOf("") }

    // Load installed user applications
    var installedApps by remember {
        mutableStateOf<List<AppInfo>>(emptyList())
    }

    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(0)
        val defaultKnown = listOf(
            AppInfo("com.google.android.youtube", "YouTube"),
            AppInfo("com.google.android.youtube.tv", "YouTube TV / SmartTube"),
            AppInfo("com.discord", "Discord"),
            AppInfo("com.discord.canary", "Discord Canary"),
            AppInfo("org.telegram.messenger", "Telegram"),
            AppInfo("org.thunderdog.challegram", "Telegram X"),
            AppInfo("com.android.chrome", "Google Chrome"),
            AppInfo("org.mozilla.firefox", "Firefox"),
            AppInfo("com.brave.browser", "Brave Browser"),
            AppInfo("com.instagram.android", "Instagram"),
            AppInfo("com.twitter.android", "X (Twitter)"),
            AppInfo("com.spotify.music", "Spotify"),
            AppInfo("com.valvesoftware.android.steam.community", "Steam"),
            AppInfo("ru.sberbankmobile", "СберБанк"),
            AppInfo("com.idamob.tinkoff.android", "Т-Банк"),
            AppInfo("ru.gosuslugi.online", "Госуслуги")
        )

        val userApps = packages
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map {
                AppInfo(
                    packageName = it.packageName,
                    appName = pm.getApplicationLabel(it).toString()
                )
            }

        val combined = (defaultKnown + userApps)
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }

        installedApps = combined
    }

    val filteredApps = installedApps.filter {
        it.appName.contains(searchQuery, ignoreCase = true) ||
        it.packageName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .padding(16.dp)
    ) {
        Text(
            text = "МАРШРУТИЗАЦИЯ ТРАФИКА",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Настройка туннелирования приложений и исключений для российских сервисов",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, if (isHybridMode) CyberCyan.copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Obsidian900)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHybridMode) "Гибридный режим (Все приложения)" else "Режим выбранных приложений (Per-App)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isHybridMode)
                                "Весь трафик идет через DPI Bypass. Банки РФ (Сбер, Т-Банк, ВТБ) и Госуслуги автоматически исключены."
                            else
                                "Только отмеченные ниже приложения направляются через туннель.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Switch(
                        checked = isHybridMode,
                        onCheckedChange = { checked ->
                            isHybridMode = checked
                            configManager.isHybridMode = checked
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Obsidian950,
                            checkedTrackColor = CyberCyan,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Obsidian800
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск приложения...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Obsidian900,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // App List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
                val isSelected = selectedPackages.contains(app.packageName)
                val isExcludedBank = AegisConfigManager.DEFAULT_EXCLUDED_PACKAGES.contains(app.packageName)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Obsidian900)
                        .border(1.dp, if (isSelected && !isHybridMode) CyberCyan.copy(alpha = 0.4f) else BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = app.appName,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            if (isExcludedBank) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "БАНК (DIRECT)",
                                    color = NeonAmber,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = app.packageName,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (!isHybridMode) {
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { isChecked ->
                                val newSet = selectedPackages.toMutableSet()
                                if (isChecked) {
                                    newSet.add(app.packageName)
                                } else {
                                    newSet.remove(app.packageName)
                                }
                                selectedPackages = newSet
                                configManager.selectedPackages = newSet
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Obsidian950,
                                checkedTrackColor = CyberCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Obsidian800
                            )
                        )
                    } else {
                        Text(
                            text = if (isExcludedBank) "ИСКЛЮЧЕН" else "ЗАЩИЩЕН",
                            color = if (isExcludedBank) NeonAmber else CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
