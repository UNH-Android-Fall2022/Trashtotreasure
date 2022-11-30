package com.newhaven.trashtotreasure.home.ui.messages

import java.util.*

data class ChatMessage(
    val text: String?,
    val user: String?,
    val reciever: String?,
    val timestamp: Date?,
    val username: String?,
    val time: String?,
    val startDate : String?
)