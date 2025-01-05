package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.Sales
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.generics.TelegramClient

class SaleSubmittedEvent(override val botId: Long): IMessageCapturingEvent {
    override val targetSubstring: String
        get() = "#sale"

    override fun consumeMessage(telegramClient: TelegramClient, message: CapturedMessageDTO) {
        transaction {
            Sales.insert {
                it[telegramId] = message.userId
                it[foundInGroup] = message.groupId
                it[description] = message.content
            }
        }
    }
}