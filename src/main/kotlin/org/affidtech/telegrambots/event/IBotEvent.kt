package org.affidtech.telegrambots.event

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

interface IBotEvent {

    val eventType: EventType

    /**
     * Process the update and execute the event
     *
     * @param telegramClient client to send requests
     * @param update   the update to process
     */
    fun processUpdate(telegramClient: TelegramClient, update: Update)

    /**
     * Defines whether the event should be skipped or not.
     * Override this function in your event implementation to filter events
     * <p>
     * For example, if you want to prevent event execution for admin members:
     * #
     * # return update.chatMember?.status == "administrator";
     * #
     *
     * @param update Received update
     * @return true if the event should be skipped, false otherwise
     * @note Default implementation doesn't filter anything
     */
    fun filter(update: Update): Boolean = false
}