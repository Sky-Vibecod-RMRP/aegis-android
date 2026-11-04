package com.sky.aegis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
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
import com.sky.aegis.model.VpsNode
import com.sky.aegis.ui.theme.*

@Composable
fun VpsNodesScreen(
    modifier: Modifier = Modifier
) {
    var nodes by remember {
        mutableStateOf(
            listOf(
                VpsNode("1", "VLESS-Reality-NL (Амстердам 10G)", "nl.aegis.net", 443, "vless", 42, "NL", isSelected = true),
                VpsNode("2", "VLESS-Reality-DE (Франкфурт Low-Ping)", "de.aegis.net", 443, "vless", 38, "DE"),
                VpsNode("3", "VLESS-Reality-FI (Хельсинки Gaming)", "fi.aegis.net", 443, "vless", 24, "FI"),
                VpsNode("4", "Trojan-TLS-US (Лос-Анджелес Stream)", "us.aegis.net", 443, "trojan", 135, "US")
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VPS / VLESS СЕРВЕРЫ",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Узлы VLESS Reality с FakeIP & UDP BBR",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = { /* Refresh pings */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Obsidian800)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Ping", tint = CyberCyan)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Obsidian900)
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Добавить подписку или vless://",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { /* Add link dialog */ },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Obsidian950, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Импорт", color = Obsidian950, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(nodes, key = { it.id }) { node ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (node.isSelected) Obsidian800 else Obsidian900)
                        .border(
                            1.dp,
                            if (node.isSelected) CyberCyan else BorderSubtle,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            nodes = nodes.map { it.copy(isSelected = it.id == node.id) }
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Obsidian700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = node.countryCode,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column {
                            Text(
                                text = node.name,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${node.protocol.uppercase()} · ${node.server}:${node.port}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${node.pingMs} ms",
                            color = if (node.pingMs != null && node.pingMs < 80) CyberEmerald else CyberAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        if (node.isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Obsidian950, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
