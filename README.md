# 🛡️ Aegis Android — Unified Anti-Censorship & DPI Bypass Control Center

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-brightgreen.svg)](https://developer.android.com/)
[![Tech Stack](https://img.shields.io/badge/Stack-Kotlin%20%7C%20Jetpack%20Compose%20%7C%20VpnService-cyan.svg)](https://kotlinlang.org/)
[![Release](https://img.shields.io/badge/Release-v1.0.1%20APK-orange.svg)](https://github.com/Sky-Vibecod-RMRP/aegis-android/releases/tag/v1.0.1)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Мобильная версия **Aegis** для Android смартфонов и планшетов. Обходит замедление **YouTube в 4K**, голосовых каналов **Discord** (UDP 50000-65535) и **Telegram** прямо на устройстве.

---

## 📥 Скачать APK на смартфон

> Прямая ссылка на установку: **[Скачать Aegis-v1.0.1.apk (31.0 МБ)](https://github.com/Sky-Vibecod-RMRP/aegis-android/releases/download/v1.0.1/Aegis-v1.0.1.apk)**  
> Все релизы доступны на странице: **[GitHub Releases](https://github.com/Sky-Vibecod-RMRP/aegis-android/releases)**

### Быстрая установка:
1. Перейдите по ссылке выше и скачайте файл **`Aegis-v1.0.1.apk`** прямо на телефон.
2. Откройте скачанный файл в шторке смартфона и подтвердите установку.
3. Запустите Aegis, нажмите центральную кнопку щита и разрешите создание VPN-соединения во всплывающем системном окне Android.

---

## ✨ Ключевые возможности

- 🚀 **Гибридный режим защиты (Smart Hybrid)**:
  - **DPI-Bypass без серверов**: Дробление TCP ClientHello и Fake SNI desync для обхода замедлений YouTube без необходимости покупки сторонних VPN.
  - **VLESS Reality / Trojan Tunnel**: Интеграция с личными VPS-серверами через современный протокол XTLS-Reality.
- 📱 **Разделение приложений (Per-App Routing)**:
  - Направляйте в туннель только YouTube, Discord, Telegram и браузер.
  - Российские банки (Сбер, Т-Банк, ВТБ, Альфа) и Госуслуги работают напрямую без задержек.
- ⚡ **Плитка в шторке (Quick Settings Tile)**:
  - Включение и отключение защиты в один тап прямо из шторки уведомлений Android.
- 📊 **Телеметрия в реальном времени**:
  - Живой мониторинг отклика до серверов YouTube (200 KB chunk), Discord Voice UDP и Telegram DC1-DC5.
- 🔋 **Zero-Battery Drain**:
  - Оптимизированный Android `VpnService` с минимальной нагрузкой на аккумулятор и защитой от выгрузки системой в спящем режиме.

---

## 🛠️ Архитектура и стек

- **Язык & UI**: Kotlin 1.9.24, Jetpack Compose, Material 3, Coroutines Flow.
- **Сетевое ядро**: Android `VpnService` L3 Virtual TUN Adapter (`198.18.0.1/15` FakeIP).
- **DPI Engine**: Локальный desync-процессор для фрагментации пакетов и внедрения фейковых заголовков.

---

## 🧑‍💻 Разработчик & Контакты

- **Автор**: Арсений (Sky) ([@Sky-Vibecod-RMRP](https://github.com/Sky-Vibecod-RMRP))
- **Telegram для связи**: [@Sledak_69](https://t.me/Sledak_69)
- **Поддержать разработку**: [DonationAlerts](https://www.donationalerts.com/r/skycode69)
- **Десктопная версия (Windows)**: [aegis-control-center-](https://github.com/Sky-Vibecod-RMRP/aegis-control-center-)

---

## 📄 Лицензия
Распространяется под свободной лицензией MIT.
