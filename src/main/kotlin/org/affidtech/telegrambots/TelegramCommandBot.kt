package org.affidtech.telegrambots

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramCommandBot(private val commandRegistry: CommandRegistry, private val processNonCommandUpdateCallback: (update: Update) -> Unit) : CommandBot, ICommandRegistry by commandRegistry {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun processNonCommandUpdate(update: Update) {
        processNonCommandUpdateCallback(update)
    }

    fun processUpdate(update: Update) {
        logger.atInfo().log("Running commands for incoming message message")
        if (update.hasMessage() && update.message.isCommand) {
            if(!commandRegistry.executeCommand(update.message)){
                processInvalidCommandUpdate(update)
            }
        } else {
            processNonCommandUpdate(update)
        }
    }

}