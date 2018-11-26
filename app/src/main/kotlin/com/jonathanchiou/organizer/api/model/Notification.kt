package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json

fun createNotification(): Notification {
    val notification = Notification()
    notification.title = "Respond to events"
    notification.text = "You've been invited to some events. " +
        "Please reply to them as soon as possible!"
    notification.actionType = "Reply now"
    notification.actionCount = 3

    return notification
}

class Notification {

    @Json(name = "title")
    var title = ""

    @Json(name = "text")
    var text = ""

    @Json(name = "action_type")
    var actionType = ""

    @Json(name = "action_count")
    var actionCount = 0
}