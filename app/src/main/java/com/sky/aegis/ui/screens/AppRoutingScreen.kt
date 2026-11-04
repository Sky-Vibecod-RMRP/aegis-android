package com.sky.aegis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.aegis.model.AppInfo
import com.sky.aegis.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoutingScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var apps by remember {
        mutableStateOf(
            listOf(
                AppInfo("com.google.android.youtube", "YouTube", isProtected = true),
                AppInfo("com.discord", "Discord", isProtected = true),
                AppInfo("org.telegram.messenger", "Telegram", isProtected = true),
                AppInfo("com.instagram.android", "Instagram", isProtected = true),
                AppInfo("com.twitter.android", "X (Twitter)", isProtected = true),
                AppInfo("ru.sberbankmobile", "СберБанк", isProtected = false),
                AppInfo("com.idamob.tinkoff.android", "Т-Банк", isProtected = false),
                AppInfo("ru.gosuslugi.online", "Госуслуги", isProtected = false),
                AppInfo("com.spotify.music", "Spotify", isProtected = true),
                AppInfo("com.valvesoftware.android.steam.community", "Steam", isProtected = true)
            )
        )
    }

    val filteredApps = apps.filter {
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
            text = "РАЗДЕЛЕНИЕ ПРИЛОЖЕНИЙ",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Выберите приложения, которые должны идти через защищенный туннель. Российские сервисы исключены для максимальной скорости.",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        // App List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.appName,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = app.packageName,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Switch(
                        checked = app.isProtected,
                        onCheckedChange = { isChecked ->
                            apps = apps.map {
                                if (it.packageName == app.packageName) it.copy(isProtected = isChecked) else it
                            }
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
    }
}
