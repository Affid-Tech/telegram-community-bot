package org.affidtech.telegrambots.community.event

import org.affidtech.exposed.postgres.removeNullable
import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.affidtech.telegrambots.community.entity.TelegramUsers
import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class UserLeftChatEvent(override val botId: Long) : IBotIdAware, IBotEvent {
    private val logger = LoggerFactory.getLogger(UserLeftChatEvent::class.java)

    override val eventType: EventType
        get() = EventType.MESSAGE

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        val leftMember = update.message?.leftChatMember ?: return
        val groupId = update.message?.chat?.id ?: return

        transaction {
            TelegramGroups.update({ TelegramGroups.id eq groupId }) {
                with(SqlExpressionBuilder) {
                    it[memberCount] = memberCount - 1
                    it[administrators] = administrators.removeNullable(leftMember.id)
                }
            }

            TelegramUsers.update({ TelegramUsers.id eq leftMember.id }) {
                it[consistsOfGroups] = consistsOfGroups.removeNullable(groupId)
            }
        }

        runCatching {
            telegramClient.execute(DeleteMessage.builder().chatId(groupId).messageId(update.message.messageId).build())
        }.onFailure {
            logger.error("Error occurred while deleting user left service message", it)
        }
    }

    override fun filter(update: Update): Boolean {
        return update.message?.chat?.isUserChat == true || update.message?.leftChatMember == null || update.message?.leftChatMember != null && isThisBot(update.message?.leftChatMember?.id)
    }
}