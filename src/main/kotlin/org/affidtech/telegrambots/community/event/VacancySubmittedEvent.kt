package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.Vacancies
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.generics.TelegramClient

class VacancySubmittedEvent(override val botId: Long): IMessageCapturingEvent {
    override val targetSubstring: String
        get() = "#vacancy"

    override fun consumeMessage(telegramClient: TelegramClient, message: CapturedMessageDTO) {
        transaction {
            Vacancies.insert {
                it[telegramId] = message.userId
                it[foundInGroup] = message.groupId
                it[description] = message.content
            }
        }
    }
}