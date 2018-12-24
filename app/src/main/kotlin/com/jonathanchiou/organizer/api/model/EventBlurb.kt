package com.jonathanchiou.organizer.api.model

import com.jonathanchiou.organizer.main.MainFeedModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

fun createUpcomingEventBlurbs(): List<EventBlurb> {
    val account0 = Account(id = 0,
                           firstName = "Handsome",
                           lastName = "Jack",
                           profileImage = "https://steamuserimages-a.akamaihd.net/ugc/259343702669198665/F7898569F100F01CB0B48F1A8BD94F87024B03E7/")

    val account1 = Account(id = 1,
                           firstName = "Claptrap",
                           lastName = "",
                           profileImage = "https://i1.wp.com/mentalmars.com/wp-content/uploads/2014/11/claptrap.jpg?resize=250%2C239")

    val account2 = Account(id = 2,
                           firstName = "MISTER",
                           lastName = "TORGUE",
                           profileImage = "https://pbs.twimg.com/profile_images/2930127149/e18533e68204c0d64c5e22acd2c01248_400x400.jpeg")

    val account3 = Account(id = 3,
                           firstName = "Shrek",
                           lastName = "",
                           profileImage = "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/13/13d15df368a3bc05132ddd1d84d57989e4228190_full.jpg")


    val date = (System.currentTimeMillis() / 1000) + 86400

    val eventBlurb0 = EventBlurb(id = 0,
                                 title = "Hey now, you're an all-star",
                                 date = date,
                                 creator = account3)
    val eventBlurb1 = EventBlurb(id = 1,
                                 title = "Let's go kill Handsome Jack!",
                                 date = date,
                                 creator = account0)
    val eventBlurb2 = EventBlurb(id = 2,
                                 title = "Pizza party",
                                 date = date,
                                 creator = account1)
    val eventBlurb3 = EventBlurb(id = 3,
                                 title = "EXPLOSIONS?",
                                 date = date,
                                 creator = account2)
    val eventBlurb4 = EventBlurb(id = 4,
                                 title = "Get your game on, go play!",
                                 date = date,
                                 creator = account3)

    return arrayListOf(eventBlurb0, eventBlurb1, eventBlurb2, eventBlurb3, eventBlurb4)
}

fun createPastEventBlurbs(): List<EventBlurb> {
    val account3 = Account(id = 3,
                           firstName = "Shrek",
                           lastName = "",
                           profileImage = "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/13/13d15df368a3bc05132ddd1d84d57989e4228190_full.jpg")


    val date = (System.currentTimeMillis() / 1000) - 86400

    val eventBlurb5 = EventBlurb(id = 5,
                                 title = "Hey now, you're a rockstar",
                                 date = date,
                                 creator = account3)
    val eventBlurb6 = EventBlurb(id = 6,
                                 title = "Get the show on, get paid!",
                                 date = date,
                                 creator = account3)
    val eventBlurb7 = EventBlurb(id = 7,
                                 title = "All that glitters is gold",
                                 date = date,
                                 creator = account3)
    val eventBlurb8 = EventBlurb(id = 8,
                                 title = "Only shooting stars break the mold...",
                                 date = date,
                                 creator = account3)
    val eventBlurb9 = EventBlurb(id = 9,
                                 title = "It's a cool place and they say it gets colder",
                                 date = date,
                                 creator = account3)

    return arrayListOf(eventBlurb5, eventBlurb6, eventBlurb7, eventBlurb8, eventBlurb9)
}

@JsonClass(generateAdapter = true)
data class EventBlurb(@Json(name = "id") val id: Int,
                      @Json(name = "title") val title: String,
                      @Json(name = "date") val date: Long,
                      @Json(name = "creator") val creator: Account): MainFeedModel