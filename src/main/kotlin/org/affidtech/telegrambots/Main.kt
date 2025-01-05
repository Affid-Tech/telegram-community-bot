package org.affidtech.telegrambots

import org.affidtech.telegrambots.community.CommunityBot
import org.affidtech.telegrambots.community.config.*
import org.telegram.telegrambots.webhook.TelegramBotsWebhookApplication
import org.telegram.telegrambots.webhook.WebhookOptions

fun main() {
    initDatabase()
    runCatching {
        TelegramBotsWebhookApplication(WebhookOptions.builder().enableRequestLogging(true).build()).use {
            it.registerBot(CommunityBot(appConfig.botConfig, appConfig.appBaseUrl))
            Runtime.getRuntime().addShutdownHook(Thread {
                println("Gracefully shutting down...")
                it.stop()
            })
            Thread.currentThread().join()
        }
    }.onFailure {
        it.printStackTrace()
    }
}