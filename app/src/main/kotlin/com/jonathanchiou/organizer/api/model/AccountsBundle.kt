package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AccountsBundle {

    @Json(name = "bundle_type")
    var bundleType = ""

    @Json(name = "accounts")
    var accounts = listOf<Account>()
}