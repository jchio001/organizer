package com.jonathanchiou.organizer.persistence

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.toDbUIModelStream(): Observable<DbUIModel<T>> {
    return this
        .map {
            DbUIModel(DbUIModel.State.SUCCESS, it)
        }
        .onErrorReturn {
            // This probably should never happen. However for the case that it does, I probably
            // need some way of logging & tracking it
            Log.e("toDbUIModelStream", it.message)
            return@onErrorReturn DbUIModel(DbUIModel.State.ERROR)
        }
        .startWith(DbUIModel(DbUIModel.State.PENDING))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

class DbUIModel<T>(val state: State) {

    enum class State {
        PENDING,
        ERROR,
        SUCCESS
    }

    var model: T? = null

    constructor(state: State, model: T): this(state) {
        this.model = model
    }
}