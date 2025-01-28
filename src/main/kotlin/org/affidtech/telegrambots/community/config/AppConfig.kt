package org.affidtech.telegrambots.community.config

data class AppConfig(
    val globalAdministratorIds: List<Long>,
    val botConfig: BotConfig,
    val appBaseUrl: String,
    val appPort: Int,
    val db: Db
)

data class BotConfig(val token: String, val path: String, val username: String, val allowedUpdates: List<String>)

val appConfig = loadConfig(AppConfig::class.java)

data class Db(val jdbcUrl: String, val username: String, val password: String, val driverClassName: String, val schema: String)