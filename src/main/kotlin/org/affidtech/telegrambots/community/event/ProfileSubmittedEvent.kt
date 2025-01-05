package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.Profiles
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.generics.TelegramClient

class ProfileSubmittedEvent(override val botId: Long) : IMessageCapturingEvent {
    override val targetSubstring: String
        get() = "#profile"

    override fun consumeMessage(telegramClient: TelegramClient, message: CapturedMessageDTO) {
        transaction{
            Profiles.insert{
                it[telegramId] = message.userId
                it[foundInGroup] = message.groupId
                it[description] = message.content
            }
        }
    }

}