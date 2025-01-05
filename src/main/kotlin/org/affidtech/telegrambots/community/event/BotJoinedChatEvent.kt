package org.affidtech.telegrambots.community.event

import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.affidtech.telegrambots.event.EventType
import org.affidtech.telegrambots.event.IBotEvent
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class BotJoinedChatEvent: IBotEvent {
    override val eventType: EventType
        get() = EventType.MY_CHAT_MEMBER

    override fun processUpdate(telegramClient: TelegramClient, update: Update) {
        transaction{
            val groupId = update.myChatMember?.chat?.id ?: return@transaction

            TelegramGroups.upsert(where = { TelegramGroups.id eq groupId }, onUpdateExclude = listOf(TelegramGroups.id)) {
                it[id] = groupId
                it[title] = update.myChatMember?.chat?.title ?: ""
                it[administrators] = fetchChatAdmins(telegramClient, groupId)
                it[memberCount] = fetchChatMemberCount(telegramClient, groupId).toLong()
                it[type] = update.myChatMember?.chat?.type
                it[username] = update.myChatMember?.chat?.userName
                it[botEnabled] = true
            }
        }
    }

    override fun filter(update: Update): Boolean {
        return update.myChatMember?.chat?.isUserChat == true || update.myChatMember?.newChatMember?.status == "kicked" || update.myChatMember?.newChatMember?.status == "left"
    }

    private fun fetchChatAdmins(telegramClient: TelegramClient, groupId: Long): List<Long> {
        val admins = telegramClient.execute(GetChatAdministrators.builder().chatId(groupId).build())
        return admins.map { it.user.id }
    }

    private fun fetchChatMemberCount(telegramClient: TelegramClient, groupId: Long): Int {
        return telegramClient.execute(GetChatMemberCount.builder().chatId(groupId).build())
    }
}