package com.sky.aegis.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sky.aegis.model.ConnectionStatus
import com.sky.aegis.ui.screens.*
import com.sky.aegis.ui.theme.*
import com.sky.aegis.vpn.AegisVpnService

class MainActivity : ComponentActivity() {

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startAegisVpnService()
        } else {
            Toast.makeText(this, "Требуется разрешение VPN для создания туннеля", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("aegis_mobile_prefs", Context.MODE_PRIVATE)
        val savedThemeId = prefs.getString("aegis_theme", "cyber") ?: "cyber"
        val initialTheme = AppTheme.values().find { it.id == savedThemeId } ?: AppTheme.CYBER

        setContent {
            var currentTheme by remember { mutableStateOf(initialTheme) }
            val colors = LocalAegisColors.current

            AegisTheme(theme = currentTheme) {
                var selectedTab by remember { mutableStateOf(NavTab.DASHBOARD) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LocalAegisColors.current.background,
                    bottomBar = {
                        NavigationBar(
                            containerColor = LocalAegisColors.current.surface,
                            tonalElevation = 0.dp
                        ) {
                            NavTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                                    label = { Text(tab.title) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = LocalAegisColors.current.primary,
                                        selectedTextColor = LocalAegisColors.current.primary,
                                        unselectedIconColor = LocalAegisColors.current.textMuted,
                                        unselectedTextColor = LocalAegisColors.current.textMuted,
                                        indicatorColor = LocalAegisColors.current.surfaceElevated
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        NavTab.DASHBOARD -> DashboardScreen(
                            currentTheme = currentTheme,
                            onThemeChange = { newTheme ->
                                currentTheme = newTheme
                                prefs.edit().putString("aegis_theme", newTheme.id).apply()
                            },
                            onToggleVpn = { toggleVpn() },
                            modifier = Modifier.padding(innerPadding)
                        )
                        NavTab.APPS -> AppRoutingScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                        NavTab.NODES -> VpsNodesScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                        NavTab.DEVELOPER -> DeveloperScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun toggleVpn() {
        val current = AegisVpnService.connectionStatus.value
        if (current == ConnectionStatus.CONNECTED) {
            val stopIntent = Intent(this, AegisVpnService::class.java).apply {
                action = AegisVpnService.ACTION_STOP
            }
            startService(stopIntent)
        } else {
            val prepareIntent = VpnService.prepare(this)
            if (prepareIntent != null) {
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                startAegisVpnService()
            }
        }
    }

    private fun startAegisVpnService() {
        val startIntent = Intent(this, AegisVpnService::class.java).apply {
            action = AegisVpnService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent)
        } else {
            startService(startIntent)
        }
    }
}

enum class NavTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Щит", Icons.Default.Shield),
    APPS("Приложения", Icons.Default.Apps),
    NODES("Серверы", Icons.Default.Dns),
    DEVELOPER("Автор", Icons.Default.Person)
}
