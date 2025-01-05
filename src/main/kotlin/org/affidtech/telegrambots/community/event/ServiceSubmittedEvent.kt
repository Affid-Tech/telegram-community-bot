package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.Services
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.generics.TelegramClient

class ServiceSubmittedEvent(override val botId: Long): IMessageCapturingEvent {
    override val targetSubstring: String
        get() = "#service"

    override fun consumeMessage(telegramClient: TelegramClient, message: CapturedMessageDTO) {
        transaction {
            Services.insert {
                it[telegramId] = message.userId
                it[foundInGroup] = message.groupId
                it[description] = message.content
            }
        }
    }
}