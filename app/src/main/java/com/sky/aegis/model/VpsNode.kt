package com.sky.aegis.model

data class VpsNode(
    val id: String,
    val name: String,
    val server: String,
    val port: Int,
    val protocol: String = "vless",
    val pingMs: Int? = null,
    val countryCode: String = "NL",
    val isSelected: Boolean = false
)
