package org.affidtech.telegrambots.community.command

import org.affidtech.telegrambots.community.entity.GlobalPropertiesTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

class SetFooterCommand : GlobalAdminOnlyCommand, IManCommand {
    companion object {
        private const val GLOBAL_FOOTER_KEY = "globalFooter"
        private const val COMMAND_IDENTIFIER = "set_footer"
        private const val COMMAND_DESCRIPTION = "sets the reply message as a global message footer. Global Admins only!"
        private val EXTENDED_DESCRIPTION = """This command sets the text of the reply message as a global message footer.
            |The text should be written in plain text or HTML. Supported syntax: https://core.telegram.org/bots/api#html-style
            |Usage:
            |  /$COMMAND_IDENTIFIER: sets the text of the reply message as a global message footer.
""".trimMargin()
    }

    override fun getCommandIdentifier() = COMMAND_IDENTIFIER

    override fun getDescription() = COMMAND_DESCRIPTION

    override fun adminOnlyCommand(telegramClient: TelegramClient, message: Message, arguments: Array<String>) {
        val footerText = message.replyToMessage?.text

        if (footerText.isNullOrBlank()) {
            telegramClient.execute(
                SendMessage.builder().text("Footer text required").chatId(message.chatId).build()
            )
            return
        }

        transaction {
            GlobalPropertiesTable.upsert(
                where = { GlobalPropertiesTable.key eq GLOBAL_FOOTER_KEY }, keys = arrayOf(GlobalPropertiesTable.key), onUpdateExclude = listOf(GlobalPropertiesTable.key)
            ) {
                it[key] = GLOBAL_FOOTER_KEY
                it[value] = footerText
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