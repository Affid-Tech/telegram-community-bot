package org.affidtech.telegrambots.community

import org.affidtech.telegrambots.TelegramWebhookEventCommandBot
import org.affidtech.telegrambots.community.command.*
import org.affidtech.telegrambots.community.config.BotConfig
import org.affidtech.telegrambots.community.event.*
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry
import org.telegram.telegrambots.meta.api.methods.GetMe
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.adminrights.ChatAdministratorRights
import org.telegram.telegrambots.meta.generics.TelegramClient

class CommunityBot(
    botConfig: BotConfig,
    basePath: String,
    client: TelegramClient = OkHttpTelegramClient(botConfig.token),
    commandRegistry: CommandRegistry = CommandRegistry(client, true) { botConfig.username }
) : TelegramWebhookEventCommandBot(
    client,
    allowCommandsWithUsername = true,
    botUsernameSupplier = { botConfig.username },
    botPath = botConfig.path,
    setWebhook = createSetWebhook(client, botConfig, basePath),
    deleteWebhook = { client.execute(DeleteWebhook()) },
    commandRegistry = commandRegistry
) {

    private val botId = client.execute(GetMe()).id

    init {
        registerEvents()
        registerCommands(commandRegistry)
        configureRequiredAdminRights()
    }

    private fun registerCommands(commandRegistry: CommandRegistry) {
        assignCommandBotOnMessageEvent { it.message?.chat?.isUserChat != true }
        registerAll(
            HelpCommand(commandRegistry),
            SetFooterCommand(),
            FanoutCommand(),
            GetChatInfosCommand(),
            SetWelcomeMessageCommand()
        )
        submitCommands()
    }

    private fun registerEvents() {
        registerAll(
            BotJoinedChatEvent(),
            BotLeftChatEvent(botId),
            ChatTitleChangedEvent(),
            UsersJoinedChatEvent(botId),
            UserLeftChatEvent(botId),
            ProfileSubmittedEvent(botId),
            ProjectSubmittedEvent(botId),
            RequestSubmittedEvent(botId),
            ResumeSubmittedEvent(botId),
            SaleSubmittedEvent(botId),
            ServiceSubmittedEvent(botId),
            VacancySubmittedEvent(botId)
        )
    }

    private fun configureRequiredAdminRights() {
        requiresAdminRights(
            ChatAdministratorRights.builder()
                .canManageChat(true)
                .isAnonymous(false)
                .canDeleteMessages(true)
                .canChangeInfo(true)
                .canInviteUsers(true)
                .canPostMessages(true)
                .canEditMessages(true)
                .canPinMessages(true)
                .canManageVideoChats(false)
                .canEditStories(false)
                .canPostStories(false)
                .canDeleteStories(false)
                .canRestrictMembers(false)
                .canPromoteMembers(false)
                .build()
        )
    }

    companion object {
        private fun createSetWebhook(client: TelegramClient, botConfig: BotConfig, basePath: String): () -> Unit =
            {
                client.execute(
                    SetWebhook.builder()
                        .allowedUpdates(botConfig.allowedUpdates)
                        .url(botUrl(basePath, botConfig))
                        .dropPendingUpdates(true)
                        .build()
                )
            }

        private fun botUrl(basePath: String, botConfig: BotConfig) =
            listOf(basePath.trimEnd('/'), botConfig.path.trimStart('/')).joinToString("/")
    }
}
