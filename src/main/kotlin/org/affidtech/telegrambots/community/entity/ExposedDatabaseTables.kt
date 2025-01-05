package org.affidtech.telegrambots.community.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ArrayColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.javatime.timestamp


object GlobalPropertiesTable : LongIdTable("global_properties") {
    val key = varchar("key", length = 255).uniqueIndex()
    val value = text("value")
}

object Profiles : LongIdTable("\"Profiles\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object Projects : LongIdTable("\"Projects\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object Requests : LongIdTable("\"Requests\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object Resume : LongIdTable("\"Resume\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object Sales : LongIdTable("\"Sales\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object Services : LongIdTable("\"Services\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

object TelegramGroups : LongIdTable(name = "\"TelegramGroups\"", columnName = "telegram_id" ) {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val wm = text("wm").default("Привет!")
    val lastWmId = long("last_wm_id").default(-1L)
    val botEnabled = bool("bot_enabled").default(true)
    val title = varchar("title", 255).default("")
    val username = varchar("username", 255).nullable()
    val type = varchar("type", 255).nullable()
    val inviteLink = varchar("invite_link", 255).nullable()
    val memberCount = long("member_count").default(0L)
    val administrators = registerColumn(name = "administrators", type = ArrayColumnType<Long, List<Long>>(delegate = LongColumnType())).nullable()
}

object TelegramUsers : LongIdTable(name = "\"TelegramUsers\"", columnName = "telegram_id") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val consistsOfGroups = registerColumn(name = "consists_of_groups", type = ArrayColumnType<Long, List<Long>>(delegate = LongColumnType())).nullable()
    val username = varchar("username", 255).nullable()
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val isBot = bool("is_bot").nullable()
}

object Vacancies : LongIdTable("\"Vacancies\"") {
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
    val description = text("description").default("")
    val telegramId = long("telegram_id")
    val foundInGroup = long("found_in_group")
}

data class TelegramGroupDto(
    val id: Long,
    val username: String?,
    val botEnabled: Boolean,
    val title: String,
    val wm: String,
    val type: String?,
    val inviteLink: String?,
    val memberCount: Long,
    val administrators: List<Long>?
)