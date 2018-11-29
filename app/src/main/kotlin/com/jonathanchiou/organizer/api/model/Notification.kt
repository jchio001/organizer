package com.jonathanchiou.organizer.api.model

import com.jonathanchiou.organizer.main.MainFeedModel
import com.squareup.moshi.Json

data class Notification(@Json(name = "title") val title: String,
                        @Json(name = "text") val text: String,
                        @Json(name = "action_type") val actionType: String,
                        @Json(name = "action_count") val actionCount: Int): MainFeedModel {
}