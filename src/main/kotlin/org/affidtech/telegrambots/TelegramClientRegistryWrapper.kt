package org.affidtech.telegrambots

import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry
import org.telegram.telegrambots.meta.generics.TelegramClient

class TelegramClientRegistryWrapper(private val telegramClient: TelegramClient, private val commandRegistry: CommandRegistry) : TelegramClient by telegramClient,
    ICommandRegistry by commandRegistry