package com.jonathanchiou.foodorganizer

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Observable<Response<T>>.toUIModelStream(): Observable<UIModel<T>> {
    return this
            .map {
                if (it.isSuccessful) {
                    return@map UIModel(State.SUCCESS, it.body())
                } else {
                    Log.e("FoodOrganizerClient", it.code().toString())
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

class FoodOrganizerClient(val foodOrganizerService: FoodOrganizerService) {

    fun connect(googleIdToken: String): Observable<UIModel<Token>> {
        return foodOrganizerService
                .connect(googleIdToken)
                .toUIModelStream()
    }

    fun getPlaces(input: String, location: String?): Observable<UIModel<List<Place>>> {
        return foodOrganizerService
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