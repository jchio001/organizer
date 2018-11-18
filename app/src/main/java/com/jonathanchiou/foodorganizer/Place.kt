package com.jonathanchiou.foodorganizer

import com.squareup.moshi.Json

class Place {

    @Json(name = "place_id")
    var place_id = ""

    @Json(name = "name")
    var name = ""
}