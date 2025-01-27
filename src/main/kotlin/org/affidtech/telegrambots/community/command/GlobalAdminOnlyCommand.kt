package org.affidtech.telegrambots.community.command

import org.affidtech.telegrambots.community.config.appConfig
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

private const val PERMISSION_DENIED = "Permission denied. Please, refer to admins if you believe something is wrong"

interface GlobalAdminOnlyCommand: IBotCommand {

    override fun processMessage(telegramClient: TelegramClient, message: Message, arguments: Array<String>) {
        if(!appConfig.globalAdministratorIds.contains(message.from?.id)){
            telegramClient.execute(SendMessage.builder().chatId(message.chatId).text(PERMISSION_DENIED).build())
        }

        adminOnlyCommand(telegramClient, message, arguments)
    }

    fun adminOnlyCommand(telegramClient: TelegramClient, message: Message, arguments: Array<String>)
}