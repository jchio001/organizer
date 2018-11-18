package com.jonathanchiou.foodorganizer

import com.squareup.moshi.Json

class JwtPayload {

    @Json(name = "id")
    var id: Int = 0

    @Json(name = "iat")
    var issuedTime: Long = 0

    @Json(name = "exp")
    var expirationTime: Long = 0
}