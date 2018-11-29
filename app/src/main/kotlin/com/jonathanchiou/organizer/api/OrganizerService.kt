package com.jonathanchiou.organizer.api

import com.jonathanchiou.organizer.api.model.Account
import com.jonathanchiou.organizer.api.model.EventBlurb
import com.jonathanchiou.organizer.api.model.Place
import com.jonathanchiou.organizer.api.model.Token
import com.jonathanchiou.organizer.scheduler.ClientEvent
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface OrganizerService {

    @PUT("connect")
    fun connect(@Query("google_id_token") googleIdToken: String): Observable<Response<Token>>

    @POST("token")
    @Headers("required: authorization", "skip-refresh: true")
    fun refreshToken(): Observable<Response<Token>>

    @GET("notification")
    @Headers("required: authorization")
    fun getNotification(): Observable<Response<Token>>

    @GET("group/{group_id}/events")
    @Headers("required: authorization")
    fun getEvents(): Observable<Response<List<EventBlurb>>>

    @GET("places")
    @Headers("required: authorization")
    fun getPlaces(@Query("input") input: String?,
                  @Query("location") location: String?): Observable<Response<List<Place>>>

    @GET("group/{group_id}/accounts")
    @Headers("required: authorization")
    fun searchAccounts(@Path("group_id") groupId: Int,
                       @Query("query") query: String?): Observable<List<Account>>

    @PUT("group/{group_id}/event")
    @Headers("required: authorization")
    fun createEvent(@Path("group_id") groupId: Int,
                    @Body event: ClientEvent): Observable<Response<EventBlurb>>
}