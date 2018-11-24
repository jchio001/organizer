package com.jonathanchiou.organizer.api.model

enum class State {
    PENDING,
    SUCCESS,
    UNSUCCESSFUL,
    ERROR_NO_NETWORK,
    ERROR_TIMEOUT,
    ERROR_UNKNOWN
}

class UIModel<T>(val state: State) {

    var model: T? = null

    constructor(state: State, model: T?) : this(state) {
        this.model = model
    }
}