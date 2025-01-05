package org.affidtech.telegrambots.community.config

data class AppConfig(
    val globalAdministratorIds: List<Long>,
    val botConfig: BotConfig,
    val appBaseUrl: String,
)

data class BotConfig(val token: String, val path: String, val username: String, val allowedUpdates: List<String>)

val appConfig = loadConfig(AppConfig::class.java)