package org.affidtech.telegrambots

import org.affidtech.telegrambots.event.EventBot
import org.affidtech.telegrambots.event.EventRegistry
import org.affidtech.telegrambots.event.IEventRegistry
import org.affidtech.telegrambots.event.eventType
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramEventBot(private val eventRegistry: EventRegistry, private val processUnhandledEventCallback: (update: Update) -> Unit) : EventBot, IEventRegistry by eventRegistry {

    override fun processEvent(update: Update): Boolean {
        return eventRegistry.executeEvent(update, update.eventType())
    }

    override fun processUnhandledEvent(update: Update) {
        processUnhandledEventCallback(update)
    }
}