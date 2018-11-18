package com.jonathanchiou.foodorganizer

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface FoodOrganizerService {

    @PUT("connect")
    fun connect(@Query("google_id_token") googleIdToken: String): Observable<Response<Token>>

    @POST("token")
    @Headers("required: authorization", "refresh-token: false")
    fun refreshToken() : Observable<Response<Token>>

    @GET("places")
    @Headers("required: authorization")
    fun getPlaces(@Query("input") input: String?,
                  @Query("location") location: String?) : Observable<Response<List<Place>>>
}