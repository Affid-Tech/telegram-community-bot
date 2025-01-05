package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class BotLeftChatEvent(override val botId: Long): IBotIdAware, IBotEvent {
    override val eventType: EventType
        get() = EventType.MESSAGE

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        val telegramId = update.message?.chat?.id ?: return

        transaction {
            TelegramGroups.update({ TelegramGroups.id eq telegramId }) {
                it[botEnabled] = false
            }
        }
    }

    override fun filter(update: Update): Boolean {
        return update.message?.chat?.isUserChat == true || !isThisBot(update.message?.leftChatMember?.id)
    }
}