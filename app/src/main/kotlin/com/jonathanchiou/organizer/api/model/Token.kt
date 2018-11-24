package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json

class Token {

    @Json(name = "token")
    var token: String = ""
}