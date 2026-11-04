package com.sky.aegis.model

data class AppInfo(
    val packageName: String,
    val appName: String,
    val isProtected: Boolean = true,
    val isSystemApp: Boolean = false
)
