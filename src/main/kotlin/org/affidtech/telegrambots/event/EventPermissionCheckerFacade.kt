package org.affidtech.telegrambots.event

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

interface EventPermissionCheckerFacade: IBotEvent {

    val delegate: IBotEvent

    override fun filter(update: Update): Boolean{
        return !sufficientPermissions(update) || delegate.filter(update)
    }

    fun sufficientPermissions(update: Update): Boolean

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        delegate.processUpdate(telegramClient, update)
    }
}