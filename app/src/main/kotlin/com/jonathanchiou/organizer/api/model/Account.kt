package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json

fun createAccounts(): List<Account> {
    val account0 = Account()
    account0.id = 0
    account0.firstName = "Erlich"
    account0.lastName = "Bachman"

    val account1 = Account()
    account1.id = 1
    account1.firstName = "Jian"
    account1.lastName = "Yang"

    val account2 = Account()
    account2.id = 2
    account2.firstName = "Richard"
    account2.lastName = "Hendricks"

    return listOf(account0, account1, account2)
}

class Account {

    @Json(name = "id")
    var id = 0

    @Json(name = "first_name")
    var firstName = ""

    @Json(name = "last_name")
    var lastName = ""

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Account) false else return id == other.id
    }

    override fun toString(): String {
        return "$firstName $lastName"
    }
}