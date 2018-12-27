package com.jonathanchiou.organizer.api.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
class Place(@Json(name = "place_id")
            val placeId: String,
            @Json(name = "name")
            val name: String): Parcelable {

    // TODO: This is kind of janky (placeId should be used as the hash code). Fix later!
    override fun equals(other: Any?): Boolean {
        if (other is String) {
            return name == other
        } else if (other == null || other !is Place) {
            return false
        }

        return name == other.name
    }
}