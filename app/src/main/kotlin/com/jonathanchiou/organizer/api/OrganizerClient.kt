package com.jonathanchiou.organizer.api

import android.util.Log
import com.jonathanchiou.organizer.api.model.*
import com.jonathanchiou.organizer.main.MainFeedModel
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
        return Observable.just(Response.success(Notification(title = "Respond to events",
                                                             text = "You've been invited to some " +
                                                                 "events. Please respond to them " +
                                                                 "as soon as possible!",
                                                             actionType = "Respond now",
                                                             actionCount = 3)))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    fun getEvents(): Observable<UIModel<List<EventBlurb>>> {
        return Observable.just(Response.success(createEventBlurbs()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    // TODO: Slightly better, but still kind of sucky.
    fun getMainFeed(): Observable<UIModel<List<MainFeedModel>?>> {
        return Observable.zip(
            getNotification(),
            getEvents(),
            BiFunction<
                UIModel<Notification>,
                UIModel<List<EventBlurb>>,
                UIModel<List<MainFeedModel>?>> { notificationUIModel, eventsUIModel ->
                var state = notificationUIModel.state
                if (eventsUIModel.state < state) {
                    state = eventsUIModel.state
                }

                val mainFeedModels = ArrayList<MainFeedModel>(3)
                notificationUIModel.model?.let {
                    mainFeedModels.add(it)
                }
                eventsUIModel.model?.let {
                    mainFeedModels.addAll(it)
                }

                return@BiFunction UIModel(state, mainFeedModels)
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

    fun createEvent(groupId: Int, clientEvent: ClientEvent): Observable<UIModel<EventBlurb>> {
        return Observable.just(Response.success(EventBlurb(id = 42,
                                                           title = "",
                                                           date = 0,
                                                           creator = createAccount())))
            .toUIModelStream()
    }
}