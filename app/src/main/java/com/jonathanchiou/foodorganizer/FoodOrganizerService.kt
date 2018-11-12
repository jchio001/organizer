package com.jonathanchiou.foodorganizer

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Query

interface FoodOrganizerService {
    @PUT("connect")
    fun connect(@Query("google_id_token") googleIdToken: String)
            : Observable<Response<Token>>
}