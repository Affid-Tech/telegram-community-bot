package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

interface IMessageCapturingEvent: IBotEvent, IBotIdAware {
    override val eventType: EventType
        get() = EventType.MESSAGE

    val targetSubstring: String

    fun consumeMessage(telegramClient: TelegramClient, message: CapturedMessageDTO)

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        capturedMessage(update.message)?.let {
            consumeMessage(telegramClient, it)
        }
    }

    override fun filter(update: Update): Boolean {
        return update.message?.chat?.isUserChat == true || isThisBot(update.message?.from?.id) || (update.message?.text ?: update.message?.caption)?.contains(targetSubstring) != true
    }

    private fun capturedMessage(message: Message): CapturedMessageDTO?{
        val content = (message.text ?: message.caption) ?: return null
        val userId = message.from?.id ?: return null
        val groupId = message.chatId ?: return null

        return CapturedMessageDTO(groupId, userId, content)
    }


}

data class CapturedMessageDTO(val groupId: Long, val userId: Long, val content: String)