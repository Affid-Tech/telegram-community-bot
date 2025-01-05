package org.affidtech.telegrambots.event

import org.telegram.telegrambots.meta.api.objects.Update

/**
 * This interface represents common functions for bots that handle events.
 * It defines methods for processing and filtering events based on the Update object.
 */
interface EventBot {

    fun processUpdate(update: Update){
        if(update.isEvent() && !filter(update)){
            if(!processEvent(update)){
                processUnhandledEvent(update)
            }
            return
        }
        processUnknownEvent(update)
    }


    fun processEvent(update: Update): Boolean

    /**
     * Processes all updates that are not mapped to a specific event.
     *
     * @param update The received update object.
     * @warning Updates that are valid but do not match any registered handler
     * won't be forwarded to this method <b>if a default action is present</b>.
     */
    fun processUnhandledEvent(update: Update)

    /**
     * This method is called when an update is received but no registered event matches it.
     * By default, it delegates to [processUnhandledEvent]. Override it to define custom behavior.
     *
     * @param update The update that triggered no matching registered event.
     */
    fun processUnknownEvent(update: Update) {
        processUnhandledEvent(update)
    }

    /**
     * Filters updates based on custom conditions. Override this method in your bot implementation
     * to skip certain updates for processing.
     * <p>
     * For example, if you want to prevent events execution for updates from group chats:
     * #
     * # return update.message?.chat?.isGroupChat ?: false
     * #
     *
     * @param update The received update object.
     * @return true if the update should be ignored by the bot and treated as unhandled,
     * false otherwise.
     * @note The default implementation does not filter any updates.
     */
    fun filter(update: Update): Boolean {
        return false
    }
}