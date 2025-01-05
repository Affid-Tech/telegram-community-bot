package org.affidtech.telegrambots.event

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * This interface represents the gateway for registering and deregistering events.
 */
interface IEventRegistry {

    /**
     * Register a default action when there is no event registered that matches the message sent.
     *
     * @param defaultConsumer Consumer to evaluate the message.
     * @note Use this method if you want your bot to execute a default action when the bot receives
     * a message that does not trigger any registered event.
     */
    fun registerDefaultAction(defaultConsumer: (TelegramClient, Update) -> Unit)

    /**
     * Register an event.
     *
     * @param botEvent The event to register.
     * @return true if the event was successfully registered, false if it was already registered.
     */
    fun register(botEvent: IBotEvent): Boolean

    /**
     * Register multiple events.
     *
     * @param botEvents The events to register.
     * @return A map indicating the result of the registration for each event.
     */
    fun registerAll(vararg botEvents: IBotEvent): Map<IBotEvent, Boolean>

    /**
     * Deregister an event.
     *
     * @param botEvent The event to deregister.
     * @return true if the event was successfully deregistered, false if it was not registered.
     */
    fun deregister(botEvent: IBotEvent): Boolean

    /**
     * Deregister multiple events.
     *
     * @param botEvents The events to deregister.
     * @return A map indicating the result of the deregistration for each event.
     */
    fun deregisterAll(vararg botEvents: IBotEvent): Map<IBotEvent, Boolean>

    /**
     * Get a collection of all registered events.
     *
     * @return A collection of registered events.
     */
    fun getRegisteredEvents(): Collection<IBotEvent>

    /**
     * Get registered events by their type.
     *
     * @return Registered events if they exist, or empty collection if they do not.
     */
    fun getRegisteredEvents(eventType: EventType): Collection<IBotEvent>

    operator fun get(eventType: EventType) = getRegisteredEvents(eventType)
}