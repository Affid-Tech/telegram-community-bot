package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.GlobalPropertiesTable
import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.affidtech.telegrambots.community.entity.TelegramUsers
import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.affidtech.telegrambots.exposed.posgtres.append
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.generics.TelegramClient

class UsersJoinedChatEvent(override val botId: Long) : IBotIdAware, IBotEvent {
    private val logger = LoggerFactory.getLogger(UsersJoinedChatEvent::class.java)

    override val eventType: EventType
        get() = EventType.MESSAGE

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        val newMembers = (update.message?.newChatMembers ?: return).apply {
            removeAll { isThisBot(it.id) }
        }

        if (newMembers.isEmpty()) {
            return
        }

        val groupId = update.message?.chat?.id ?: return

        transaction {
            TelegramGroups.update({ TelegramGroups.id eq groupId }) {
                with(SqlExpressionBuilder) {
                    it[memberCount] = memberCount + newMembers.size.toLong()
                }
            }

            TelegramUsers.batchUpsert(
                newMembers,
                onUpdate = {
                    it[TelegramUsers.consistsOfGroups] = TelegramUsers.consistsOfGroups.append(groupId)
                }
            ) {
                this[TelegramUsers.id] = it.id
                this[TelegramUsers.username] = it.userName
                this[TelegramUsers.firstName] = it.firstName
                this[TelegramUsers.lastName] = it.lastName
                this[TelegramUsers.isBot] = it.isBot
                this[TelegramUsers.consistsOfGroups] = listOf(groupId)
            }
        }

        val wmData = welcomeMessageData(groupId)
        val footer = globalFooter()

        runCatching {
            telegramClient.execute(DeleteMessage.builder().chatId(groupId).messageId(update.message.messageId).build())
            if(wmData.second > -1){
                telegramClient.execute(DeleteMessage.builder().chatId(groupId).messageId(wmData.second.toInt()).build())
            }

            telegramClient.execute(
                SendMessage.builder().chatId(groupId).disableNotification(true).text(generateUserWelcomeMessage(newMembers, wmData.first, footer)).parseMode("html").build()
            ).let { message ->
                transaction{
                    TelegramGroups.update({ TelegramGroups.id eq groupId }) {
                        it[lastWmId] = message.messageId.toLong()
                    }
                }
            }

        }.onFailure {
            logger.error("Error occurred while greeting new members", it)
        }

    }

    override fun filter(update: Update): Boolean {
        return update.message?.chat?.isUserChat == true || update.message?.newChatMembers?.isEmpty() != false
    }


    private fun generateUserWelcomeMessage(users: List<User>, commonWM: String, footer: String): String {
        return """${users.joinToString("\n") { user -> userMention(user.id, listOf(user.firstName, user.lastName).filter { !it.isNullOrBlank() }.joinToString(" ")) }}
            |$commonWM
            |
            |$footer
        """.trimMargin()
    }

    private fun globalFooter(): String {
        return transaction {
            val all = GlobalPropertiesTable.selectAll().where { GlobalPropertiesTable.key eq "globalFooter" }.map { it[GlobalPropertiesTable.value] }
            all.first()
        }
    }

    private fun welcomeMessageData(groupId: Long): Pair<String, Long> {
        return transaction {
            TelegramGroups.select(TelegramGroups.wm, TelegramGroups.lastWmId).where { TelegramGroups.id eq groupId }
                .map { Pair(it[TelegramGroups.wm], it[TelegramGroups.lastWmId]) }
        }.first()
    }

    private fun userMention(userId: Long, name: String): String {
        return "<a href=\"tg://user?id=$userId\">$name</a>"
    }
}