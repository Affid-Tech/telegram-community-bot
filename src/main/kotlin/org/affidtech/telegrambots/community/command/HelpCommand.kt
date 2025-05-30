package org.affidtech.telegrambots.community.command

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

private const val COMMAND_IDENTIFIER = "help"
private const val COMMAND_DESCRIPTION = "shows all commands. Use /help [command] for more info"
private val EXTENDED_DESCRIPTION = """This command displays all commands the bot has to offer.
    |Usage:
    |  /help [command] - display deeper information about the command
    |  /help - display general info about all commands
""".trimMargin()

class HelpCommand(private val commandRegistry: ICommandRegistry) : ManCommand(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, EXTENDED_DESCRIPTION) {

    private val logger = LoggerFactory.getLogger(javaClass)
    /**
     * Returns the command and description of all supplied commands as a formatted String
     * @param botCommands the Commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    fun getHelpText(vararg botCommands: IBotCommand): String {
        val reply = StringBuilder()
        for (com in botCommands) {
            reply.append(com.toString()).append(System.lineSeparator()).append(System.lineSeparator())
        }
        return reply.toString()
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     * @param botCommands a collection of commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    fun getHelpText(botCommands: Collection<IBotCommand>): String {
        return getHelpText(*botCommands.toTypedArray<IBotCommand>())
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     * @param registry a commandRegistry which commands are formatted into the String
     * @return a formatted String containing command and description for all supplied commands
     */
    fun getHelpText(registry: ICommandRegistry): String {
        return getHelpText(registry.registeredCommands)
    }

    /**
     * Reads the extended Description from a BotCommand. If the Command is not of Type [IManCommand], it calls toString();
     * @param command a command the extended Descriptions is read from
     * @return the extended Description or the toString() if IManCommand is not implemented
     */
    fun getManText(command: IBotCommand): String {
        return if (command is IManCommand) getManText(command as IManCommand) else command.toString()
    }

    /**
     * Reads the extended Description from a BotCommand;
     * @param command a command the extended Descriptions is read from
     * @return the extended Description
     */
    fun getManText(command: IManCommand): String {
        return command.toMan()
    }


    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<String?>) {
        if (arguments.isNotEmpty()) {
            val command = commandRegistry.getRegisteredCommand(arguments[0])
            if(command == null){
                 telegramClient.execute(SendMessage.builder().chatId(chat.id).text("Unknown command").parseMode("HTML").build())
                 return
            }
            val reply = getManText(command)
            try {
                telegramClient.execute(SendMessage.builder().chatId(chat.id).text(reply).parseMode("HTML").build())
            } catch (e: TelegramApiException) {
                logger.error(e.localizedMessage, e)
            }
        } else {
            val reply = getHelpText(commandRegistry)
            try {
                telegramClient.execute(SendMessage.builder().chatId(chat.id).text(reply).parseMode("HTML").build())
            } catch (e: TelegramApiException) {
                logger.error(e.localizedMessage, e)
            }
        }
    }
}
