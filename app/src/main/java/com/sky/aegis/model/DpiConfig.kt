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
            description = "Проверенный пресет для операторов РФ (МТС, Мегафон, Билайн, Т2, Wi-Fi): сплит TCP, disoob и сброс SACK",
            cliArgs = "-s 1 -q 1 -Y",
            splitPos = 1,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "autodesync-torst",
            name = "Auto-Desync Torst (YouTube 4K & Discord)",
            description = "Автоматический десинк torst с перестановкой TCP для стабильного видео 4K 60FPS",
            cliArgs = "-A torst -s 1 -d 2 -Y",
            splitPos = 1,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "disorder-stream",
            name = "Disorder2 Stream (Высокая скорость)",
            description = "Перестановка TCP-сегментов без фейков, минимальная задержка в звонках и стримах",
            cliArgs = "-s 1 -d 2",
            splitPos = 1
        ),
        DpiStrategy(
            id = "oob-aggressive",
            name = "Агрессивный OOB (Max Desync)",
            description = "OOB-десинк и сплит пакетов для глухих блокировок и зарубежных сайтов",
            cliArgs = "-s 2 -o 1 -d 2 -Y",
            splitPos = 2
        )
    )

    fun findById(id: String): DpiStrategy {
        return ALL.find { it.id == id } ?: ALL.first()
    }
}
