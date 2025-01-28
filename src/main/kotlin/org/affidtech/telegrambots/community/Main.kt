package org.affidtech.telegrambots.community

import org.affidtech.telegrambots.community.config.appConfig
import org.affidtech.telegrambots.community.config.initDatabase
import org.telegram.telegrambots.webhook.TelegramBotsWebhookApplication
import org.telegram.telegrambots.webhook.WebhookOptions

class Main{

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
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
    }

}
