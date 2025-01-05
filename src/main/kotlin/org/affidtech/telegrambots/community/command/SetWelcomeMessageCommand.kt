package org.affidtech.telegrambots.community.command

import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

class SetWelcomeMessageCommand : IBotCommand, IManCommand {
    companion object {
        private const val COMMAND_IDENTIFIER = "set_welcome_message"
        private const val COMMAND_DESCRIPTION = "Sets the replied message text as a welcome message for the chat"
        private val EXTENDED_DESCRIPTION = """This command Sets the provided text as a welcome message for the provided chat. 
            |Welcome message consist will be concatenated with the current footer each time.
            |The text should be written in plain text or HTML. Supported syntax: https://core.telegram.org/bots/api#html-style
            |Usage:
            |  /$COMMAND_IDENTIFIER [chat_username]: sets the welcome message of the chat by username.
            |  /$COMMAND_IDENTIFIER -id [chat_id]: sets the welcome message of the chat by id.
        """.trimMargin()
    }

    override fun getCommandIdentifier() = COMMAND_IDENTIFIER

    override fun getDescription() = COMMAND_DESCRIPTION

    override fun processMessage(telegramClient: TelegramClient, message: Message, arguments: Array<String>) {
        val welcomeMessage = message.replyToMessage?.text

        if (arguments.isEmpty()) {
            telegramClient.execute(
                SendMessage.builder().text("Chat username or id required. Try /get_chat_infos to see the available chats").chatId(message.chatId).build()
            )
            return
        }

        if (welcomeMessage.isNullOrBlank()) {
            telegramClient.execute(
                SendMessage.builder().text("Welcome message required").chatId(message.chatId).build()
            )
            return
        }

        val byId = arguments.any { it == "-id" }
        val chatIdentifier = arguments.find { it != "-id" }

        if (chatIdentifier == null) {
            telegramClient.execute(
                SendMessage.builder().text("Chat ${if (byId) "id" else "username"} required").chatId(message.chatId).build()
            )
            return
        }

        transaction {
            TelegramGroups.update(where = { if (byId) TelegramGroups.id eq chatIdentifier.toLong() else TelegramGroups.username eq chatIdentifier }) {
                it[wm] = welcomeMessage
            }
        }
    }

    override fun toString(): String {
        return "/$COMMAND_IDENTIFIER - $COMMAND_DESCRIPTION"
    }

    override fun getExtendedDescription() = EXTENDED_DESCRIPTION

    override fun toMan() = """
        |<b>$COMMAND_IDENTIFIER</b>
        |-----------------
        |$EXTENDED_DESCRIPTION
        """.trimMargin()
}