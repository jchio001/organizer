package com.jonathanchiou.organizer.api.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

fun createAccount(): Account {
    return Account(id = 0,
                   firstName = "Erlich",
                   lastName = "Bachman",
                   profileImage = "")
}

fun createAccounts(): List<Account> {
    return listOf(Account(id = 0,
                          firstName = "Erlich",
                          lastName = "Bachman",
                          profileImage = ""),
                  Account(id = 1,
                          firstName = "Jian",
                          lastName = "Yang",
                          profileImage = ""),
                  Account(id = 2,
                          firstName = "Richard",
                          lastName = "Hendricks",
                          profileImage = ""))
}

@JsonClass(generateAdapter = true)
@Parcelize
class Account(@Json(name = "id") val id: Int,
              @Json(name = "first_name") val firstName: String,
              @Json(name = "last_name") val lastName: String,
              @Json(name = "profile_image") val profileImage: String):
    Parcelable {

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Account) false else return id == other.id
    }

    override fun toString(): String {
        return "$firstName $lastName"
    }
}