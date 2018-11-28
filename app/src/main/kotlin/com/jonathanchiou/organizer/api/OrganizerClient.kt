package com.jonathanchiou.organizer.api

import android.util.Log
import com.jonathanchiou.organizer.api.model.*
import com.jonathanchiou.organizer.scheduler.ClientEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

fun <T> Observable<Response<T>>.toUIModelStream(): Observable<UIModel<T>> {
    return this
        .map {
            if (it.isSuccessful) {
                return@map UIModel(State.SUCCESS, it.body())
            } else {
                Log.e("OrganizerClient", it.code().toString())
                return@map UIModel(State.UNSUCCESSFUL, null as T)
            }
        }
        .onErrorReturn {
            when (it) {
                is UnknownHostException -> UIModel(State.ERROR_NO_NETWORK, null)
                is SocketTimeoutException -> UIModel(State.ERROR_TIMEOUT, null)
                else -> UIModel(State.ERROR_UNKNOWN, null)
            }
        }
        .startWith(UIModel(State.PENDING, null as T))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

class OrganizerClient(val organizerService: OrganizerService) {

    fun connect(googleIdToken: String): Observable<UIModel<Token>> {
        return organizerService
            .connect(googleIdToken)
            .toUIModelStream()
    }

    fun getNotification(): Observable<UIModel<Notification>> {
        return Observable.just(Response.success(createNotification()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    fun getEvents(): Observable<UIModel<List<Event>>> {
        return Observable.just(Response.success(createEvents()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    // TODO: Make that BiFunction code less like a bulldog and more like a normal dog.
    fun getMainFeed(): Observable<UIModel<Pair<Notification?, List<Event>?>>> {
        return Observable.zip(
            getNotification(),
            getEvents(),
            BiFunction<
                UIModel<Notification>,
                UIModel<List<Event>>,
                UIModel<Pair<Notification?, List<Event>?>>> { notificationUIModel, eventsUIModel ->
                var state = notificationUIModel.state
                if (eventsUIModel.state < state) {
                    state = eventsUIModel.state
                }

                val pair = Pair(notificationUIModel.model, eventsUIModel.model)
                return@BiFunction UIModel(state, pair)
            })
    }

    fun getPlaces(input: String, location: String?): Observable<UIModel<List<Place>>> {
        return organizerService
            .getPlaces(if (!input.isEmpty()) input else null, location)
            .toUIModelStream()
    }

    fun searchAccounts(groupId: Int, query: String?): Observable<UIModel<List<Account>>> {
        return Observable.just(Response.success(createAccounts()))
            .toUIModelStream()
    }

    fun createEvent(groupId: Int, clientEvent: ClientEvent): Observable<UIModel<Event>> {
        return Observable.just(Response.success(Event()))
            .toUIModelStream()
    }
}