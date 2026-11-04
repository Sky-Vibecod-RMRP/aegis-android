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
            id = "multisplit-discord",
            name = "MultiSplit + Discord Voice (По умолчанию)",
            description = "Дробление ClientHello по позициям 2 и 3, авто-десинк torst и tlsrec для Discord Voice UDP и YouTube 4K",
            cliArgs = "-s 2 -d 3 -a torst --tlsrec 1+s",
            splitPos = 2,
            isDiscordVoiceOptimized = true
        ),
        DpiStrategy(
            id = "fakeds-sni",
            name = "Fake Desync + Google SNI",
            description = "Отправка поддельного TCP-пакета с заниженным TTL и валидным SNI www.google.com для отравления таблиц ТСПУ",
            cliArgs = "-s 3 -f -1 -t 3 --fake-sni www.google.com",
            splitPos = 3,
            fakeSni = "www.google.com"
        ),
        DpiStrategy(
            id = "disorder2-stream",
            name = "Disorder2 High-Speed 4K",
            description = "Перестановка сегментов TCP, оптимизированная под 4K 60FPS видеопотоки YouTube",
            cliArgs = "-s 1 -d 2",
            splitPos = 1
        ),
        DpiStrategy(
            id = "aggressive-desync",
            name = "Агрессивный обход (Max Desync)",
            description = "Максимальный desync: OOB, заниженный TTL и перестановка пакетов",
            cliArgs = "-s 2 -d 2 -e 1 -f -1 -t 3 -a torst",
            splitPos = 2
        )
    )

    fun findById(id: String): DpiStrategy {
        return ALL.find { it.id == id } ?: ALL.first()
    }
}
