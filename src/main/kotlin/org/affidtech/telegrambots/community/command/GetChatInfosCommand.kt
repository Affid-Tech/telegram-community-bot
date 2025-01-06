package org.affidtech.telegrambots.community.command

import org.affidtech.exposed.postgres.containsNullable
import org.affidtech.telegrambots.community.entity.TelegramGroupDto
import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient

class GetChatInfosCommand : ManCommand(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, EXTENDED_DESCRIPTION) {
    companion object {
        private const val COMMAND_IDENTIFIER = "get_chat_infos"
        private const val COMMAND_DESCRIPTION = "Returns all the chat infos"
        private val EXTENDED_DESCRIPTION = """
    |Returns info of all the chats user has access to. 
    |Usage:
    |  /$COMMAND_IDENTIFIER: Returns all the chat infos.
""".trimMargin()
    }

    override fun execute(telegramClient: TelegramClient, user: User?, chat: Chat?, arguments: Array<String>) {
        val userId = user?.id ?: return
        val chatId = chat?.id ?: return

        val groups = transaction {
            TelegramGroups.selectAll().where { TelegramGroups.administrators.containsNullable(userId) }.map {
                TelegramGroupDto(
                    id = it[TelegramGroups.id].value,
                    username = it[TelegramGroups.username],
                    botEnabled = it[TelegramGroups.botEnabled],
                    title = it[TelegramGroups.title],
                    wm = it[TelegramGroups.wm],
                    type = it[TelegramGroups.type],
                    inviteLink = it[TelegramGroups.inviteLink],
                    memberCount = it[TelegramGroups.memberCount],
                    administrators = it[TelegramGroups.administrators],
                )
            }
        }

        val responseText = "Available groups:\n" + (groups.joinToString(separator = "\n") {
            """  <b>${it.title}:</b>
                |    ${groupDetail("Id", it.id.toString())}
                |    ${groupDetail("Username", tgLink(it.username), wrapper = {str -> str})}
                |    ${groupDetail("Bot Enabled", it.botEnabled.toString())}
                |    ${groupDetail("Welcome Message", it.wm, wrapper = this::blockquote)}
                |    ${groupDetail("Group Type", it.type)}
                |    ${groupDetail("Invite Link", it.inviteLink)}
                |    ${groupDetail("Member Count", it.memberCount.toString())}
                |    ${groupDetail("Administrators", listOfCode(it.administrators?.map(Long::toString) ?: emptyList()), wrapper = {str -> str})}
            """.trimMargin()
        }.ifEmpty { "No groups found where you are an administrator." })

        // Send the response message to the chat
        telegramClient.execute(SendMessage.builder().chatId(chatId).text(responseText).parseMode("html").build())
    }

    private fun code(text: String) = "<code>$text</code>"

    private fun tgLink(tgUsername: String?) = tgUsername?.let { "<a href='https://t.me/$it'>$it</a>" }

    private fun blockquote(text: String) = "<blockquote expandable>$text</blockquote>"

    private fun listOfCode(elements: List<String>) = elements.joinToString { code(it) }

    private fun groupDetail(propName: String, value: String?, wrapper: (String) -> String = this::code): String{
        return "<i>$propName:</i> ${wrapper(value ?: "Unknown")}"
    }
}



