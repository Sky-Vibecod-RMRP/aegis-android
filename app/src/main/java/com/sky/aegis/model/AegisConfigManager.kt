package com.sky.aegis.model

import android.content.Context
import android.content.SharedPreferences

class AegisConfigManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("aegis_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_HYBRID_MODE = "key_hybrid_mode"
        const val KEY_SELECTED_PACKAGES = "key_selected_packages"
        const val KEY_ACTIVE_PRESET_ID = "key_active_preset_id"
        const val KEY_CUSTOM_FLAGS = "key_custom_flags"
        const val KEY_DNS_PRIMARY = "key_dns_primary"
        const val KEY_DNS_SECONDARY = "key_dns_secondary"

        val DEFAULT_EXCLUDED_PACKAGES = setOf(
            "ru.sberbankmobile",
            "com.idamob.tinkoff.android",
            "ru.gosuslugi.online",
            "ru.vtb24.mobilebanking.android",
            "ru.alfabank.mobile.android",
            "com.ftc.mportal.nspk.sbp",
            "ru.raiffeisennews",
            "ru.gazprombank.android.mobilebank.app",
            "ru.yoomoney.android"
        )

        val DEFAULT_TARGET_PACKAGES = setOf(
            "com.google.android.youtube",
            "com.google.android.youtube.tv",
            "com.android.chrome",
            "com.discord",
            "com.discord.canary",
            "com.discord.ptb",
            "org.telegram.messenger",
            "org.thunderdog.challegram",
            "com.spotify.music",
            "com.instagram.android",
            "com.twitter.android"
        )
    }

    var isHybridMode: Boolean
        get() = prefs.getBoolean(KEY_HYBRID_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_HYBRID_MODE, value).apply()

    var selectedPackages: Set<String>
        get() = prefs.getStringSet(KEY_SELECTED_PACKAGES, null) ?: DEFAULT_TARGET_PACKAGES
        set(value) = prefs.edit().putStringSet(KEY_SELECTED_PACKAGES, value).apply()

    var activePresetId: String
        get() = prefs.getString(KEY_ACTIVE_PRESET_ID, "multisplit-discord") ?: "multisplit-discord"
        set(value) = prefs.edit().putString(KEY_ACTIVE_PRESET_ID, value).apply()

    var customFlags: String
        get() = prefs.getString(KEY_CUSTOM_FLAGS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_FLAGS, value).apply()

    var dnsPrimary: String
        get() = prefs.getString(KEY_DNS_PRIMARY, "1.1.1.1") ?: "1.1.1.1"
        set(value) = prefs.edit().putString(KEY_DNS_PRIMARY, value).apply()

    var dnsSecondary: String
        get() = prefs.getString(KEY_DNS_SECONDARY, "8.8.8.8") ?: "8.8.8.8"
        set(value) = prefs.edit().putString(KEY_DNS_SECONDARY, value).apply()
}
