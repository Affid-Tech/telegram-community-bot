package org.affidtech.telegrambots.event

import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Represents the type of events triggered by an Update object in Telegram.
 */
enum class EventType {
    MESSAGE,
    INLINE_QUERY,
    CHOSEN_INLINE_QUERY,
    CALLBACK_QUERY,
    EDITED_MESSAGE,
    CHANNEL_POST,
    EDITED_CHANNEL_POST,
    SHIPPING_QUERY,
    PRE_CHECKOUT_QUERY,
    POLL,
    POLL_ANSWER,
    MY_CHAT_MEMBER,
    CHAT_MEMBER,
    CHAT_JOIN_REQUEST,
    MESSAGE_REACTION,
    MESSAGE_REACTION_COUNT,
    CHAT_BOOST,
    REMOVED_CHAT_BOOST,
    BUSINESS_CONNECTION,
    BUSINESS_MESSAGE,
    EDITED_BUSINESS_MESSAGE,
    DELETED_BUSINESS_MESSAGES,
    PURCHASED_PAID_MEDIA,
    UNKNOWN
}

fun Update.isEvent(): Boolean = eventType() != EventType.UNKNOWN

/**
 * Determines the event type based on the properties of the Update object.
 */
fun Update.eventType(): EventType {
    return when {
        hasMessage() -> EventType.MESSAGE
        hasInlineQuery() -> EventType.INLINE_QUERY
        hasChosenInlineQuery() -> EventType.CHOSEN_INLINE_QUERY
        hasCallbackQuery() -> EventType.CALLBACK_QUERY
        hasEditedMessage() -> EventType.EDITED_MESSAGE
        hasChannelPost() -> EventType.CHANNEL_POST
        hasEditedChannelPost() -> EventType.EDITED_CHANNEL_POST
        hasShippingQuery() -> EventType.SHIPPING_QUERY
        hasPreCheckoutQuery() -> EventType.PRE_CHECKOUT_QUERY
        hasPoll() -> EventType.POLL
        hasPollAnswer() -> EventType.POLL_ANSWER
        hasMyChatMember() -> EventType.MY_CHAT_MEMBER
        hasChatMember() -> EventType.CHAT_MEMBER
        hasChatJoinRequest() -> EventType.CHAT_JOIN_REQUEST
        messageReaction != null -> EventType.MESSAGE_REACTION
        messageReactionCount != null -> EventType.MESSAGE_REACTION_COUNT
        chatBoost != null -> EventType.CHAT_BOOST
        removedChatBoost != null -> EventType.REMOVED_CHAT_BOOST
        hasBusinessConnection() -> EventType.BUSINESS_CONNECTION
        hasBusinessMessage() -> EventType.BUSINESS_MESSAGE
        hasEditedBusinessMessage() -> EventType.EDITED_BUSINESS_MESSAGE
        hasDeletedBusinessMessage() -> EventType.DELETED_BUSINESS_MESSAGES
        hasPaidMediaPurchased() -> EventType.PURCHASED_PAID_MEDIA
        else -> EventType.UNKNOWN
    }
}