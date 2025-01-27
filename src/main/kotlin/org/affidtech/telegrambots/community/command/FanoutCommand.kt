package org.affidtech.telegrambots.community.command

import org.affidtech.telegrambots.community.entity.GlobalPropertiesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.meta.generics.TelegramClient


private const val COMMAND_IDENTIFIER = "fanout"
private const val COMMAND_DESCRIPTION = "sends a copy of the reply message to all community chats."
private val EXTENDED_DESCRIPTION = """This command sends a copy of the reply message to all community chats.
    |The text should be written in plain text or HTML. Supported syntax: https://core.telegram.org/bots/api#html-style
    |Usage:
    |  /$COMMAND_IDENTIFIER: sends the text attaching the global footer.
    |  /$COMMAND_IDENTIFIER -no-footer: sends the text without attaching the footer.
    |  /$COMMAND_IDENTIFIER -preview: sends the text into this chat. Can be combined with other flags.
    |  /$COMMAND_IDENTIFIER -pin: pins the message.
    |  /$COMMAND_IDENTIFIER -silent: send a silent message with no sound notification.
""".trimMargin()

class FanoutCommand : IBotCommand, IManCommand {
    private val logger: Logger = LoggerFactory.getLogger(FanoutCommand::class.java)

    override fun getCommandIdentifier() = COMMAND_IDENTIFIER

    override fun getDescription() = COMMAND_DESCRIPTION

    override fun processMessage(telegramClient: TelegramClient, message: Message, arguments: Array<String>) {
        val fanoutMessage = message.replyToMessage?.text

        if (fanoutMessage.isNullOrBlank()) {
            telegramClient.execute(
                SendMessage.builder().text("Message required").chatId(message.chatId).build()
            )
            return
        }

        val footerRequired = arguments.none { it.equals("-no-footer", ignoreCase = true) }
        val preview = arguments.any { it.equals("-preview", ignoreCase = true) }
        val pin = arguments.any { it.equals("-pin", ignoreCase = true) }
        val silent = arguments.any { it.equals("-silent", ignoreCase = true) }

        val targetChats = if (preview) listOf(message.chatId) else message.from?.id?.let { allChats(it) } ?: return

        runCatching {
            targetChats.map {
                telegramClient.execute(
                    SendMessage.builder().text(addFooter(fanoutMessage, footerRequired)).chatId(it).disableNotification(silent).parseMode("html").build()
                )
            }.also {
                if (pin) {
                    it.forEach { message ->
                        telegramClient.execute(PinChatMessage.builder().messageId(message.messageId).chatId(message.chatId).disableNotification(silent).build())
                    }
                }
            }

        }.onFailure { exception ->
            (exception as? TelegramApiRequestException)?.let {
                if (it.errorCode == 400) {
                    telegramClient.execute(
                        SendMessage.builder().text("Bad Input. Details: \n${it.apiResponse}\n\nCheck https://limits.tginfo.me/en").chatId(message.chatId).build()
                    )
                }
            } ?: logger.error("Unknown error occurred while sending a fanout message", exception)

        }
    }

    private fun addFooter(text: String, footerRequired: Boolean): String {
        if (!footerRequired) {
            return text
        }

        return "$text\n\n${globalFooter()}"
    }

    private fun allChats(adminId: Long): List<Long> {
        return getManagedGroups(adminId).map { it.id }
    }

    private fun globalFooter(): String {
        return transaction {
            val all = GlobalPropertiesTable.selectAll().where { GlobalPropertiesTable.key eq "globalFooter" }.map { it[GlobalPropertiesTable.value] }
            all.first()
        }
    }

    override fun getExtendedDescription() = EXTENDED_DESCRIPTION


    override fun toString(): String {
        return "/$COMMAND_IDENTIFIER - $COMMAND_DESCRIPTION"
    }

    override fun toMan() = "<b>$COMMAND_IDENTIFIER</b>\n-----------------\n$EXTENDED_DESCRIPTION"

}