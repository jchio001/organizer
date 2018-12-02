package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Place {

    @Json(name = "place_id")
    var placeId = ""

    @Json(name = "name")
    var name = ""

    override fun toString(): String {
        return name
    }

    // TODO: This is kind of janky (placeId should be used as the hash code). Fix later!
    override fun equals(other: Any?): Boolean {
        if (other is String) {
            return name == other
        } else if (other == null || other !is Place) {
            return false;
        }

        return name == other.name
    }
}