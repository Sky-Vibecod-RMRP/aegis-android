package com.sky.aegis.model

data class DpiStrategy(
    val id: String,
    val name: String,
    val description: String,
    val cliArgs: String,
    val splitPos: Int = 2,
    val fakeSni: String = "www.google.com",
    val isDiscordVoiceOptimized: Boolean = true
)

object DpiPresets {
    val ALL = listOf(
        DpiStrategy(
            id = "universal-mobile",
            name = "Мобильный обход (YouTube + Discord + TG)",
            description = "Чистый TCP Disorder — стабильный обход YouTube 4K, Discord и зарубежных сайтов без потери пакетов",
            cliArgs = "-s 1 -d 2",
            splitPos = 1,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "disorder-fast",
            name = "Disorder Fast (Split 1, Disorder 1)",
            description = "Минимальная задержка, перестановка первых сегментов TCP",
            cliArgs = "-s 1 -d 1",
            splitPos = 1
        ),
        DpiStrategy(
            id = "autodesync-torst",
            name = "Auto-Desync Torst (YouTube 4K & Discord)",
            description = "Автоматический десинк torst с перестановкой TCP для стабильного видео 4K 60FPS",
            cliArgs = "-A torst -s 1 -d 2",
            splitPos = 1,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "multisplit",
            name = "MultiSplit (Сложные блокировки)",
            description = "Множественное дробление ClientHello для обхода жестких фильтров ТСПУ",
            cliArgs = "-s 1 -d 2 -s 3",
            splitPos = 1
        )
    )

    fun findById(id: String): DpiStrategy {
        return ALL.find { it.id == id } ?: ALL.first()
    }
}
