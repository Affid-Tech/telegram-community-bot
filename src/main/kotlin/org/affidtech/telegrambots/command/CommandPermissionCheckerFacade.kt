package org.affidtech.telegrambots.command

import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

interface CommandPermissionCheckerFacade: IBotCommand {

    val delegate: IBotCommand

    fun sufficientPermissions(message: Message): Boolean

    override fun processMessage(telegramClient: TelegramClient, message: Message, arguments: Array<String>) {
        if(!sufficientPermissions(message)){
            return
        }

        delegate.processMessage(telegramClient, message, arguments)
    }
}