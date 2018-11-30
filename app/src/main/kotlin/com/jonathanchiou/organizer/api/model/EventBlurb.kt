package com.jonathanchiou.organizer.api.model

import com.jonathanchiou.organizer.main.MainFeedModel
import com.squareup.moshi.Json

fun createEventBlurbs(): List<EventBlurb> {
    val account0 = Account()
    account0.id = 0
    account0.firstName = "Handsome"
    account0.lastName = "Jack"
    account0.profileImage = "https://steamuserimages-a.akamaihd.net/ugc/259343702669198665/F7898569F100F01CB0B48F1A8BD94F87024B03E7/"

    val account1 = Account()
    account1.id = 1
    account1.firstName = "Claptrap"
    account1.lastName = ""
    account1.profileImage = "https://i1.wp.com/mentalmars.com/wp-content/uploads/2014/11/claptrap.jpg?resize=250%2C239"

    val date = (System.currentTimeMillis() / 1000) + 86400

    val eventBlurb0 = EventBlurb(id = 0,
                                 title = "Let's go kill Handsome Jack!",
                                 date = date,
                                 creator = account0)
    val eventBlurb1 = EventBlurb(id = 1,
                                 title = "Pizza party",
                                 date = date,
                                 creator = account1)

    return arrayListOf(eventBlurb0, eventBlurb1)
}

data class EventBlurb(@Json(name = "id") val id: Int,
                      @Json(name = "title") val title: String,
                      @Json(name = "date") val date: Long,
                      @Json(name = "creator") val creator: Account) : MainFeedModel