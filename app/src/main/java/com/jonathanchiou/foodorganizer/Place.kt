package com.jonathanchiou.foodorganizer

import com.squareup.moshi.Json

class Place {

    @Json(name = "place_id")
    var placeId = ""

    @Json(name = "name")
    var name = ""

    override fun toString(): String {
        return name
    }
}