package com.jonathanchiou.organizer

import com.squareup.moshi.Json

class Token {

    @Json(name = "token")
    var token: String = ""
}