package com.jonathanchiou.foodorganizer

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

    fun connect(googleIdToken: String) : Observable<UIModel<Token>> {
        return foodOrganizerService
                .connect(googleIdToken)
                .toUIModelStream()
    }
}