package org.affidtech.telegrambots.community.command

import org.affidtech.exposed.postgres.containsNullable
import org.affidtech.telegrambots.community.entity.TelegramGroupDto
import org.affidtech.telegrambots.community.entity.TelegramGroups
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun getManagedGroups(adminId: Long, showDisabled: Boolean = false): List<TelegramGroupDto> {
    return transaction {
        TelegramGroups.selectAll().where { botEnabledCheck(showDisabled) and TelegramGroups.administrators.containsNullable(adminId) }.map {
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
}

private fun botEnabledCheck(showDisabled: Boolean): Op<Boolean> = if (showDisabled) {
    Op.TRUE
} else {
    TelegramGroups.botEnabled eq true
}
