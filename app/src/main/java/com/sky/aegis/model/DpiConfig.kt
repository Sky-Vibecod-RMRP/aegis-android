package com.sky.aegis.model

data class DpiStrategy(
    val id: String,
    val name: String,
    val description: String,
    val splitPos: Int = 2,
    val fakeSni: String = "www.google.com",
    val fakeTlsPayloadHex: String? = null,
    val isDiscordVoiceOptimized: Boolean = true
)

object DpiPresets {
    val ALL = listOf(
        DpiStrategy(
            id = "multisplit-discord",
            name = "MultiSplit + Discord Voice (Default)",
            description = "Дробление ClientHello по позициям 2 и SNI + STUN пейлоад для Discord Voice UDP",
            splitPos = 2,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "fakeds-sni",
            name = "Fake Desync + Google SNI",
            description = "Отправка пакета с заниженным TTL и валидным SNI www.google.com",
            splitPos = 3,
            fakeSni = "www.google.com"
        ),
        DpiStrategy(
            id = "disorder2-stream",
            name = "Disorder2 High-Speed 4K",
            description = "Перестановка сегментов TCP, оптимизированная под 4K 60FPS стримы YouTube",
            splitPos = 1
        )
    )
}
