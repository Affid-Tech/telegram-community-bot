package org.affidtech.telegrambots.community.event

interface IBotIdAware {

    val botId: Long

    fun isThisBot(id: Long?) = id == botId

}