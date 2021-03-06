package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class JwtPayload {

    @Json(name = "sub")
    var sub: Int = 0

    @Json(name = "iat")
    var issuedTime: Long = 0

    @Json(name = "exp")
    var expirationTime: Long = 0
}