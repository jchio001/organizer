package com.jonathanchiou.organizer.api.model

import com.squareup.moshi.Json

fun createEvents(): List<Event> {
    val account0 = Account()
    account0.id = 0
    account0.firstName = "Erlich"
    account0.lastName = "Bachman"

    val account1 = Account()
    account1.id = 1
    account1.firstName = "Jian"
    account1.lastName = "Yang"

    val accountBundle0 = AccountsBundle()
    accountBundle0.bundleType = "Attendees"
    accountBundle0.accounts = listOf(account1)

    val accountBundle1 = AccountsBundle()
    accountBundle1.bundleType = "Attendees"
    accountBundle1.accounts = listOf(account0)

    val event0 = Event()
    event0.id = 0
    event0.title = "Let's go kill Handsome Jack!"
    event0.date = (System.currentTimeMillis() / 1000) - 86400
    event0.creator = account0
    event0.associatedAccounts = accountBundle0

    val event1 = Event()
    event1.id = 1
    event1.title = ""
    event1.date = (System.currentTimeMillis() / 1000) - 86400
    event1.creator = account1
    event1.associatedAccounts = accountBundle1

    return arrayListOf(event0, event1)
}

class Event {

    @Json(name = "id")
    var id = 0

    @Json(name = "title")
    var title = ""

    @Json(name = "date")
    var date = 0L

    @Json(name = "creator")
    var creator: Account? = null

    @Json(name = "associated_accounts")
    var associatedAccounts: AccountsBundle? = null
}