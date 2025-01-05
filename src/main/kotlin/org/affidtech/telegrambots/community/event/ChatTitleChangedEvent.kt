package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class ChatTitleChangedEvent: IBotEvent {
    override val eventType: EventType
        get() = EventType.MESSAGE

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        transaction {
            val telegramId = update.message?.chat?.id ?: return@transaction
            val newTitle = update.message?.newChatTitle ?: return@transaction
            TelegramGroups.update({ TelegramGroups.id eq telegramId }) {
                it[title] = newTitle
            }
        }
    }

    override fun filter(update: Update): Boolean {
        return update.message?.newChatTitle == null
    }
}