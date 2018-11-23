package com.jonathanchiou.foodorganizer

import com.squareup.moshi.Json

class Token {

    @Json(name = "token")
    var token: String = ""
}