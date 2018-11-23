package com.jonathanchiou.foodorganizer

/**
 * A model that represents a local version for event. Typically used to encapsulate events being
 * created locally by the client. This model should NEVER be returned by [FoodOrganizerClient] or
 * [FoodOrganizerService].
 */
data class ClientEvent(val title: String,
                       val scheduledTime: Long,
                       val invitedAccounts: List<Account>,
                       val placeId: String)