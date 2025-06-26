package org.affidtech.telegrambots

import org.affidtech.telegrambots.command.TelegramCommandBot
import org.affidtech.telegrambots.event.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry
import org.telegram.telegrambots.meta.api.methods.adminrights.SetMyDefaultAdministratorRights
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.adminrights.ChatAdministratorRights
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats
import org.telegram.telegrambots.meta.generics.TelegramClient
import org.telegram.telegrambots.webhook.TelegramWebhookBot
import java.util.*

private val log = LoggerFactory.getLogger(TelegramWebhookEventCommandBot::class.java)

/**
 * TelegramWebhookEventCommandBot combines webhook bot functionality, command handling,
 * and event handling using a centralized event registry.
 *
 * @property telegramClient The Telegram client used to send requests.
 * @property allowCommandsWithUsername Whether to allow commands with bot usernames.
 * @property botUsernameSupplier A supplier for the bot's username.
 * @property botPath The bot's webhook path.
 * @property setWebhook Functionality to configure the webhook.
 * @property deleteWebhook Functionality to remove the webhook configuration.
 */
open class TelegramWebhookEventCommandBot(
    private val telegramClient: TelegramClient,
    private val allowCommandsWithUsername: Boolean,
    private val botUsernameSupplier: () -> String,
    private val botPath: String,
    private val setWebhook: () -> Unit = { },
    private val deleteWebhook: () -> Unit = { },
    private val eventRegistry: EventRegistry = EventRegistry(telegramClient),
    private val commandRegistry: CommandRegistry = CommandRegistry(
        telegramClient, allowCommandsWithUsername, botUsernameSupplier
    ),
    private val commandBot: TelegramCommandBot = TelegramCommandBot(commandRegistry = commandRegistry) { _ -> },
    private val eventBot: TelegramEventBot = TelegramEventBot(eventRegistry = eventRegistry) { _ -> }
) : TelegramWebhookBot, CommandBot by commandBot, ICommandRegistry by commandBot, EventBot by eventBot, IEventRegistry by eventBot {


    fun assignCommandBotOnMessageEvent(filterCommands: (Update) -> Boolean = { false }) {
        register(EventToCommandAdapter(commandBot, filterCommands))
    }

    override fun consumeUpdate(update: Update): BotApiMethod<*>? {
        eventBot.processUpdate(update)
        return null
    }

    override fun runSetWebhook() {
        setWebhook()
    }

    override fun runDeleteWebhook() {
        deleteWebhook()
    }

    override fun getBotPath(): String {
        return botPath
    }

    fun submitCommands() {
        telegramClient.execute(
            SetMyCommands.builder()
                .commands(commandRegistry.registeredCommands.map { BotCommand(it.commandIdentifier, it.description) })
                .scope(BotCommandScopeAllPrivateChats()).build()
        )
    }

    fun requiresAdminRights(rights: ChatAdministratorRights) {
        telegramClient.execute(SetMyDefaultAdministratorRights.builder().rights(rights).build())
    }

    private class EventToCommandAdapter(private val commandBot: TelegramCommandBot, private val filterCommands: (Update) -> Boolean = { false }) : IBotEvent {
        override val eventType: EventType
            get() = EventType.MESSAGE

        override fun processUpdate(telegramClient: TelegramClient, update: Update) {
            runCatching {
                commandBot.processUpdate(update)
            }.onFailure {
                val errorId = UUID.randomUUID()
                log.error("Error occurred while processing command. Error ID: {}", errorId, it)
                runCatching {
                    telegramClient.execute(
                        SendMessage.builder().chatId(update.message.chatId).text("Error occurred while processing command. Please contact support. Error ID: $errorId").build()
                    )
                }
            }
        }

        override fun filter(update: Update): Boolean {
            return filterCommands(update)
        }
    }

}