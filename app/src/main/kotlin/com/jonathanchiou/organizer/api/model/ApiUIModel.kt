package com.jonathanchiou.organizer.api.model

import android.util.Log
import com.jonathanchiou.organizer.api.model.ApiUIModel.State
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Observable<Response<T>>.toApiUIModelStream(): Observable<ApiUIModel<T>> {
    return this
        .map {
            if (it.isSuccessful) {
                return@map ApiUIModel(ApiUIModel.State.SUCCESS, it.body())
            } else {
                Log.e("OrganizerClient", it.code().toString())
                return@map ApiUIModel(ApiUIModel.State.UNSUCCESSFUL, null as T)
            }
        }
        .onErrorReturn {
            when (it) {
                is UnknownHostException -> ApiUIModel(ApiUIModel.State.ERROR_NO_NETWORK, null)
                is SocketTimeoutException -> ApiUIModel(ApiUIModel.State.ERROR_TIMEOUT, null)
                else -> ApiUIModel(ApiUIModel.State.ERROR_UNKNOWN, null)
            }
        }
        .startWith(ApiUIModel(ApiUIModel.State.PENDING, null as T))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun lowestState(vararg states: State): State {
    if (states.isEmpty()) {
        throw IllegalStateException("No states supplied")
    }

    var lowestState = states[0]
    for (i in 1 until states.size) {
        val currentState = states[i]
        if (currentState.ordinal < lowestState.ordinal) {
            lowestState = currentState
        }
    }

    return lowestState
}

class ApiUIModel<T>(val state: State) {

    enum class State {
        PENDING,
        UNSUCCESSFUL,
        ERROR_NO_NETWORK,
        ERROR_TIMEOUT,
        ERROR_UNKNOWN,
        SUCCESS,
    }

    var model: T? = null

    constructor(state: State, model: T?): this(state) {
        this.model = model
    }
}