package com.sky.aegis.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.aegis.ui.theme.*

@Composable
fun DeveloperScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val tgUsername = "@Sledak_69"
    val tgLink = "https://t.me/Sledak_69"
    val donateLink = "https://www.donationalerts.com/r/skycode69"
    val githubLink = "https://github.com/Sky-Vibecod-RMRP/aegis-android"

    fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Скопировано: $text", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Developer Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Obsidian900)
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CyberCyan, CyberMagenta)
                            )
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Obsidian950),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Dev",
                            tint = CyberCyan,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Арсений (Sky)",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "@Sky-Vibecod-RMRP · Lead Developer",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Создатель Aegis Control Center. Разработка системных сетевых утилит, низкоуровневых драйверов перехвата пакетов и обхода DPI для Windows & Android.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Quick Actions (Telegram & DonationAlerts)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // DonationAlerts Button
            Button(
                onClick = { openBrowser(donateLink) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberAmber
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Obsidian950)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Поддержать проект (DonationAlerts)",
                    color = Obsidian950,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Telegram Button
            OutlinedButton(
                onClick = { openBrowser(tgLink) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyberCyan
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = CyberCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Написать в Telegram: $tgUsername",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // GitHub Button
            OutlinedButton(
                onClick = { openBrowser(githubLink) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GitHub Репозиторий проекта",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        // Tech Stack Overview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Obsidian900)
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ТЕХНОЛОГИЧЕСКИЙ СТЕК ANDROID",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                TechBadge("Kotlin & Jetpack Compose", "Реактивный киберпанк интерфейс, Material 3, нулевая задержка")
                TechBadge("Android VpnService", "Системный L3 виртуальный TUN адаптер без Root прав")
                TechBadge("C/Go DPI-Bypass Core", "Локальный Desync ClientHello, дробление пакетов и Fake SNI")
                TechBadge("Quick Settings Tile API", "Управление защитой в один тап прямо из шторки Android")
            }
        }
    }
}

@Composable
private fun TechBadge(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian800)
            .padding(10.dp)
    ) {
        Text(title, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(desc, color = TextMuted, fontSize = 10.sp, lineHeight = 14.sp)
    }
}
