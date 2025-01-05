package org.affidtech.telegrambots.event

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * This class manages all the events for a bot. You can register and deregister event handlers based on event keys.
 */
class EventRegistry(
    private val telegramClient: TelegramClient, private var defaultConsumer: ((TelegramClient, Update) -> Unit)? = null
) : IEventRegistry {

    private val logger: Logger = LoggerFactory.getLogger(EventRegistry::class.java)

    private val eventHandlers: MutableMap<EventType, MutableList<IBotEvent>> = mutableMapOf()

    /**
     * Registers a default action for unhandled events.
     */
    override fun registerDefaultAction(defaultConsumer: (TelegramClient, Update) -> Unit) {
        this.defaultConsumer = defaultConsumer
    }

    /**
     * Registers an individual event.
     *
     * @param botEvent The event to register.
     * @return true if the event was successfully registered; false if it already exists.
     */
    override fun register(botEvent: IBotEvent): Boolean {
        val eventType = botEvent.eventType
        val handlers = eventHandlers.computeIfAbsent(eventType) { mutableListOf() }
        return if (handlers.contains(botEvent)) {
            false // Already registered
        } else {
            handlers.add(botEvent)
            true
        }
    }

    /**
     * Registers multiple events.
     *
     * @param botEvents The events to register.
     * @return A map indicating the result of registering each event.
     */
    override fun registerAll(vararg botEvents: IBotEvent): Map<IBotEvent, Boolean> {
        return botEvents.associateWith { register(it) }
    }

    /**
     * Deregisters an individual event.
     *
     * @param botEvent The event to deregister.
     * @return true if the event was successfully deregistered; false if it was not found.
     */
    override fun deregister(botEvent: IBotEvent): Boolean {
        val eventType = botEvent.eventType
        val handlers = eventHandlers[eventType] ?: return false
        val removed = handlers.remove(botEvent)
        if (handlers.isEmpty()) {
            eventHandlers.remove(eventType) // Clean up empty lists
        }
        return removed
    }

    /**
     * Deregisters multiple events.
     *
     * @param botEvents The events to deregister.
     * @return A map indicating the result of deregistering each event.
     */
    override fun deregisterAll(vararg botEvents: IBotEvent): Map<IBotEvent, Boolean> {
        return botEvents.associateWith { deregister(it) }
    }

    /**
     * Gets all registered events.
     *
     * @return A collection of all registered events.
     */
    override fun getRegisteredEvents(): Collection<IBotEvent> {
        return eventHandlers.values.flatten()
    }

    /**
     * Gets a specific registered event by its identifier.
     *
     * @param eventType The type of the event .
     * @return The registered event, or null if not found.
     */
    override fun getRegisteredEvents(eventType: EventType): Collection<IBotEvent> {
        return eventHandlers[eventType] ?: emptyList()
    }

    /**
     * Executes the appropriate handlers for a given message.
     */
    fun executeEvent(update: Update, eventType: EventType): Boolean {
        val handlers = eventHandlers[eventType]?.toList()

        if (handlers != null) {
            handlers.filter { !it.filter(update) }.forEach {
                logger.atInfo().log("Starting botEvent: ${it::class.java.simpleName}")
                it.processUpdate(telegramClient, update)
            }
            return true
        }

        // Trigger fallback/default consumer if no handler is found
        defaultConsumer?.let {
            it(telegramClient, update)
            return true
        }

        return false
    }


}