package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Token {

    @Json(name = "token")
    var token: String = ""
}